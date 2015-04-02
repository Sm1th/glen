import java.net.*;
import java.io.*;

public class GameServer {

	private ServerSocket serverSocket;
	//the client sockets are an array because the GameServer class accepts a variable number of players
	private Socket[] clientSocketArray;
	private int portNumber;
	private boolean serverIsRunning = false;

	public String playerOne = "QuartoRandomAgent";
	public String playerTwo = "QuartoRandomAgent";
	public String stateFileName = null;
	public boolean automatic = false;

	public static final String INFORM_PLAYER_NUMBER_HEADER = "PLAYER: ";

	public GameServer() {
		//this.portNumber = portNumber;
	}

	public GameServer(String player1, String player2, String state) {
		stateFileName = state;
		playerOne = player1;
		playerTwo = player2;
		automatic = true;
		//this.portNumber = portNumber;
	}
	
	public boolean startServer(int portNumber) {
		try {
			this.portNumber = portNumber;
			
			this.serverSocket = new ServerSocket(portNumber);
			
			System.out.println("Server started");
			this.serverIsRunning = true;
			return true;
		} catch (Exception e) {
			System.out.println("Could not start server with port " + this.portNumber);
			e.printStackTrace();
			this.serverIsRunning = false;
			return false;
		}
	}
	
	public boolean closeServer() {
		System.out.println("Server Closing");
		try {
			serverSocket.close();
			this.serverIsRunning = false;
			return true;
		} catch (Exception e) {
			System.out.println("Failed to close server");
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean acceptClients(int numberOfPlayers) {
	
		this.clientSocketArray = new Socket[numberOfPlayers];
		
		for(int i = 0; i < numberOfPlayers; i++) {
			try {
				if (automatic){
					if (i==0){
						if (stateFileName != null){
							new ProcessBuilder("java", playerOne, "localhost", stateFileName).start();
						}else{
							new ProcessBuilder("java", playerOne, "localhost").start();
						}
					}else{	
						if (stateFileName != null){
							new ProcessBuilder("java", playerTwo, "localhost", stateFileName).start();
						}else{
							new ProcessBuilder("java", playerTwo, "localhost").start();
						}
					}
				}
				this.clientSocketArray[i] = this.serverSocket.accept();
				this.writeToPlayer((i+1), INFORM_PLAYER_NUMBER_HEADER + (i+1));
				
			} 
			catch (Exception e) {
				System.out.println("Accept failed on port: " + this.portNumber + " with Player " + (i+1));
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public boolean writeToPlayer(int playerNumber, String message) {
		
		if (playerNumber > 0 && this.getNumberOfPlayers() >= playerNumber) {
		
			try {
				PrintWriter out = new PrintWriter(this.clientSocketArray[playerNumber - 1].getOutputStream(), true);
				out.println(message);
				System.out.println("Server sent Player " + playerNumber + " the following message: " + message);
			} catch (Exception e) {
				System.out.println("Writing failed for Player: " + playerNumber);
				e.printStackTrace();
				return false;
			}
			
		} else {
			System.out.println("Invalid Player number: " + playerNumber);
			return false;
		}
		
		return true;

	}
	
	public boolean writeToAllPlayers(String message) {
	
		PrintWriter out;
		for(int i = 0; i < this.getNumberOfPlayers(); i++) {
		
			try {
				out = new PrintWriter(this.clientSocketArray[i].getOutputStream(), true);
				out.println(message);
			} catch (Exception e) {
				System.out.println("Writing failed for Player: " + i);
				e.printStackTrace();
				return false;
			}
			
		}
		System.out.println("Server sent all players: " + message);
		
		return true;
	}
	
	public String listenToPlayer(int playerNumber, int timeOutTime) {
		
		if (playerNumber > 0 && this.getNumberOfPlayers() >= playerNumber) {
			try {
				//set the timeout for user input
				this.clientSocketArray[playerNumber - 1].setSoTimeout(timeOutTime);
				BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocketArray[playerNumber - 1].getInputStream()));
				String input = in.readLine();
				System.out.println("Server received message from player " + playerNumber + ": " + input);
				return input;
			} catch (Exception e) {
				System.out.println("\u001B[31mNo input from Player: " + playerNumber + " within specified time limit\u001B[0m");
			}
			
			return null;
			
		} else {
			System.out.println("Invalid Player number: " + playerNumber);
			return "error";
		}

	}
	
	public int getNumberOfPlayers() {
		return this.clientSocketArray.length;
	}
	
	
	public int getPort() {
		return this.portNumber;
	}
	
	public boolean isServerRunning() {
		return this.serverIsRunning;
	}

}
