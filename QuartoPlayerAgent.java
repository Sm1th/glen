public class QuartoPlayerAgent extends QuartoAgent {


    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors), you need to pass the parameters required.
        super(gameClient, stateFileName);
    }

    //MAIN METHOD
    public static void main(String[] args) {
        //start the server
        GameClient gameClient = new GameClient();

        String ip = null;
        String stateFileName = null;
        //IP must be specified
        if(args.length > 0) {
            ip = args[0];
        } else {
            System.out.println("No IP Specified");
            System.exit(0);
        }
        if (args.length > 1) {
            stateFileName = args[1];
        }

        gameClient.connectToServer(ip, 4321);
        QuartoRandomAgent quartoAgent = new QuartoRandomAgent(gameClient, stateFileName);
        quartoAgent.play();

        gameClient.closeConnection();

    }


    /*
	 * Do Your work here
	 * The server expects a binary string, e.g.   10011
	 */
    @Override
    protected String pieceSelectionAlgorithm() {
        //some useful lines:
        //String BinaryString = String.format("%5s", Integer.toBinaryString(pieceID)).replace(' ', '0');

        QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);

        //do work
        int pieceID = copyBoard.chooseRandomPieceNotPlayed(100);
        String BinaryString = String.format("%5s", Integer.toBinaryString(pieceID)).replace(' ', '0');
        return BinaryString;
    }

    /*
     * Do Your work here
     * The server expects a move in the form of:   row,column
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        //do work

        int[] move = new int[2];
        QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
        move = copyBoard.chooseRandomPositionNotPlayed(100);

        return move[0] + "," + move[1];
    }

    public static double monteCarlo(QuartoBoard board, int simulations){
        double sum = 0;
        for (int i=0;i<simulations;i++){
            QuartoBoard copyBoard = new QuartoBoard(board);
            int turn = 1;
            while (true){
                int pieceId = copyBoard.chooseRandomPieceNotPlayed(100);
                int[] move = copyBoard.chooseRandomPositionNotPlayed(100);
                copyBoard.insertPieceOnBoard(move[0], move[1], pieceId);
                if (checkIfGameIsWon(copyBoard)){
                    sum+=turn;
                    break;
                }else if (copyBoard.checkIfBoardIsFull()){
                    break;
                }
                turn = 0-turn;
            }
        }
        return sum/simulations;
    }

    public static boolean checkIfGameIsWon(QuartoBoard board) {
        for(int i = 0; i < NUMBER_OF_ROWS; i++) {
            if (board.checkRow(i)) {
                return true;
            }

        }
        for(int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (board.checkColumn(i)) {
                return true;
            }

        }
        if (board.checkDiagonals()) {
            return true;
        }

        return false;
    }

}
