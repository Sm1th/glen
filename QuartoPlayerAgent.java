import java.lang.*;
import java.util.Arrays;
public class QuartoPlayerAgent extends QuartoAgent {

    private final int DEPTH = 3;
    private long starttime;
    private long endtime;
    private final int WIN_SCORE = 1000;
    private final int LOSE_SCORE = -1000;
    private final int simulations = 1;

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
        QuartoPlayerAgent quartoAgent = new QuartoPlayerAgent(gameClient, stateFileName);
        quartoAgent.play();

        gameClient.closeConnection();

    }


    /*
	 * Do Your work here
	 * The server expects a binary string, e.g.   10011
	 */
    @Override
    protected String pieceSelectionAlgorithm() {
        starttime = System.nanoTime();
        System.out.println("Choosing piece....");
        int pieceID=-1;//THIS SHOULD NEVER ACTUALLY BE USED
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double value = Double.NEGATIVE_INFINITY;
        for (int i=0;i<this.quartoBoard.getNumberOfPieces();i++){
            QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
            if (!copyBoard.isPieceOnBoard(i)){
                double successorValue = minChoosePosition(copyBoard, i, alpha, beta, 0);
                if (successorValue>value){
                    value = successorValue;
                    pieceID = i;
                }
                if (value>=beta){
                    break;
                }
                if (value>alpha){
                    alpha=value;
                }
            }
        }
        endtime = System.nanoTime();
        System.out.println("Took " + (endtime-starttime)/1000000 + " milliseconds to choose piece.");
        String BinaryString = String.format("%5s", Integer.toBinaryString(pieceID)).replace(' ', '0');
        return BinaryString;
    }

    private double minChoosePosition(QuartoBoard board, int pieceID, double alpha, double beta, int depth){
        if (depth>=DEPTH){
            return monteCarlo(board, false, false, pieceID);
        }
        double value = Double.POSITIVE_INFINITY;
        for (int row=0;row<board.getNumberOfRows();row++){
            for (int col=0;col<board.getNumberOfColumns();col++){
                if (!board.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    if (checkIfGameIsWon(copyBoard)){
                        return LOSE_SCORE;
                    }
                    double successorValue = minChoosePiece(copyBoard, alpha, beta, depth+1);
                    if (successorValue<value){
                        value=successorValue;
                    }
                    if (value<=alpha){
                        return value;
                    }
                    if (value<beta){
                        beta=value;
                    }
                }
            }
        }
        return value;
    }

    private double minChoosePiece(QuartoBoard board, double alpha, double beta, int depth){
        if (depth>=DEPTH){
            return monteCarlo(board, false, true, 0);
        }
        double value = Double.POSITIVE_INFINITY;
        for (int i=0;i<board.getNumberOfPieces();i++){
            QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
            if (!copyBoard.isPieceOnBoard(i)){
                double successorValue = maxChoosePosition(copyBoard, i, alpha, beta, depth+1);
                if (successorValue<value){
                    value=successorValue;
                }
                if (value<=alpha){
                    return value;
                }
                if (value<beta){
                    beta=value;
                }
            }
        }
        return value;
    }

    private double maxChoosePosition(QuartoBoard board, int pieceID, double alpha, double beta, int depth){
        if (depth>=DEPTH){
            return monteCarlo(board, true, false, pieceID);
        }
        double value = Double.NEGATIVE_INFINITY;
        for (int row=0;row<board.getNumberOfRows();row++){
            for (int col=0;col<board.getNumberOfColumns();col++){
                if (!board.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(board);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    if (checkIfGameIsWon(copyBoard)){
                        return WIN_SCORE;
                    }
                    double successorValue = maxChoosePiece(copyBoard, alpha, beta, depth+1);
                    if (successorValue>value){
                        value=successorValue;
                    }
                    if (value>=beta){
                        return value;
                    }
                    if (value>alpha){
                        alpha=value;
                    }
                }
            }
        }
        return value;
    }

    private double maxChoosePiece(QuartoBoard board, double alpha, double beta, int depth){
        if (depth>=DEPTH){
            return monteCarlo(board, true, true, 0);
        }
        double value = Double.NEGATIVE_INFINITY;
        for (int i=0;i<board.getNumberOfPieces();i++){
            if (!board.isPieceOnBoard(i)){
                QuartoBoard copyBoard = new QuartoBoard(board);
                double successorValue = minChoosePosition(copyBoard, i, alpha, beta, depth+1);
                if (successorValue>value){
                    value=successorValue;
                }
                if (value>=beta){
                    return value;
                }
                if (value>alpha){
                    alpha=value;
                }
            }
        }
        return value;
    }

    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        starttime = System.nanoTime();
        //do work
        System.out.println("Choosing move....");
        int[] move = new int[2];
        double value = Double.NEGATIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        for (int row=0;row<this.quartoBoard.getNumberOfRows();row++){
            for (int col=0;col<this.quartoBoard.getNumberOfColumns();col++){
                if (!this.quartoBoard.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    if (checkIfGameIsWon(copyBoard)){
                        return row + "," + col;
                    }
                    double successorValue = maxChoosePiece(copyBoard, alpha, beta, 0);
                    if (successorValue>value){
                        value=successorValue;
                        move[0]=row;
                        move[1]=col;
                    }
                    if (value>=beta){
                        return move[0] + "," + move[1];
                    }
                    if (value>alpha){
                        alpha=value;
                    }
                }
            }
        }
        endtime = System.nanoTime();
        System.out.println("Took " + (endtime-starttime)/1000000 + " milliseconds to choose position.");
        return move[0] + "," + move[1];
    }

    private double monteCarlo(QuartoBoard board, boolean isGlensTurn, boolean isChoosePiece, int pieceId){
        int sum = 0;

        for (int i=0;i<simulations;i++){
            QuartoBoard copyBoard = new QuartoBoard(board);
            sum+=runSimulation(copyBoard, isGlensTurn, isChoosePiece, pieceId);
        }

        return sum/simulations;
    }

    private double runSimulation(QuartoBoard board, boolean isGlensTurn, boolean isChoosePiece, int givenPieceId){
        int pieceId;
        if (isChoosePiece){
            pieceId = board.chooseRandomPieceNotPlayed(100);
            isGlensTurn=!isGlensTurn;
        }else{
            pieceId = givenPieceId;
        }
        while (!board.checkIfBoardIsFull()){
            int[] move = board.chooseRandomPositionNotPlayed(100);
            board.insertPieceOnBoard(move[0], move[1], pieceId);
            if (checkIfGameIsWon(board)){
                if (isGlensTurn){
                    return WIN_SCORE;
                }else{
                    return LOSE_SCORE;
                }
            }
            pieceId = board.chooseRandomPieceNotPlayed(100);
            isGlensTurn=!isGlensTurn;
        }
        return LOSE_SCORE;
    }


    private boolean checkIfGameIsWon(QuartoBoard board) {
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

    protected int getHeuristicValue(QuartoBoard board) {
    	int count1 = 0, count2 = 0, count3 = 0, count4 = 0;

        for(int i = 0; i < board.getNumberOfRows(); i++) {
    		int nCol = getNumberOfIdenticalCharacteristicsInColumn(board, i);
    		switch(nCol){
    			case 1:
    				count1++;
    				break;
    			case 2:
    				count2++;
    				break;
    			case 3:
    				count3++;
    				break;
    			case 4:
    				count4++;
    				break;
    		}

    		int nRow = getNumberOfIdenticalCharacteristicsInRow(board, i);
    		switch(nRow){
    			case 1:
    				count1++;
    				break;
    			case 2:
    				count2++;
    				break;
    			case 3:
    				count3++;
    				break;
    			case 4:
    				count4++;
    				break;
    		}

    	} //end for loop

        int nLowDiag = getNumberOfIdenticalCharacteristicsInLowDiagonal(board);
        switch(nLowDiag) {
            case 1:
                count1++;
                break;
            case 2:
                count2++;
                break;
            case 3:
                count3++;
                break;
            case 4:
                count4++;
                break;
        }


        int nHighDiag = getNumberOfIdenticalCharacteristicsInHighDiagonal(board);
        switch(nHighDiag) {
            case 1:
                count1++;
                break;
            case 2:
                count2++;
                break;
            case 3:
                count3++;
                break;
            case 4:
                count4++;
                break;
        }

	    return (-1*count4) + (3*count3) + (2*count2) + (1*count1);

    }


	protected int getNumberOfIdenticalCharacteristicsInColumn(QuartoBoard board, int column) {

		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		boolean[] initialCharacteristics = null;
		int i;
		for (i = 0; i < board.getNumberOfRows(); i++) {
			if (board.getPieceOnPosition(i,column) != null) {
				initialCharacteristics = board.getPieceOnPosition(i, column).getCharacteristicsArray();
				break;
			}
		}
		if (initialCharacteristics == null) {
			return 0;
		}


		for(int row = i; row < board.getNumberOfRows(); row++) {
			QuartoPiece piece = board.getPieceOnPosition(row, column);
			if(piece == null) {
				continue;
			}
			characteristics = piece.getCharacteristicsArray();

			for(int k = 0; k < commonCharacteristics.length; k++) {
				if(characteristics[k] == initialCharacteristics[k] && commonCharacteristics[k] != -1){
					commonCharacteristics[k]++;
				}
				else {
					commonCharacteristics[k] = -1;
				}
			}
		}

        Arrays.sort(commonCharacteristics);
		int returnVal = commonCharacteristics[commonCharacteristics.length - 1];
		return (returnVal == -1) ? 0 : returnVal;
	}

	protected int getNumberOfIdenticalCharacteristicsInRow(QuartoBoard board, int row) {

		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		boolean[] initialCharacteristics = null;
		int i;
		for (i = 0; i < board.getNumberOfColumns(); i++) {
			if (board.getPieceOnPosition(row,i) != null) {
				initialCharacteristics = board.getPieceOnPosition(row, i).getCharacteristicsArray();
				break;
			}
		}
		if (initialCharacteristics == null) {
			return 0;
		}

		for(int col = i; col < board.getNumberOfColumns(); col++) {
			QuartoPiece piece = board.getPieceOnPosition(row, col);
			if(piece == null) {
				continue;
			}
			characteristics = piece.getCharacteristicsArray();

			for(int k = 0; k < commonCharacteristics.length; k++) {
				if(characteristics[k] == initialCharacteristics[k] && commonCharacteristics[k] != -1){
					commonCharacteristics[k]++;
				}
				else {
					commonCharacteristics[k] = -1;
				}
			}
		}

        Arrays.sort(commonCharacteristics);
		int returnVal = commonCharacteristics[commonCharacteristics.length - 1];
		return (returnVal == -1) ? 0 : returnVal;

	}

	protected int getNumberOfIdenticalCharacteristicsInLowDiagonal(QuartoBoard board) {
		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		boolean[] initialCharacteristics = null;
		int i, j;
		for(i = 0, j = 0; i < board.getNumberOfRows(); i++, j++) {
			if (board.getPieceOnPosition(i,j) != null) {
				initialCharacteristics = board.getPieceOnPosition(i, j).getCharacteristicsArray();
				break;
			}
		}
		if (initialCharacteristics == null) {
			return 0;
		}



		for(int row = i, column = j; row < board.getNumberOfRows(); row++, column++) {
			QuartoPiece piece = board.getPieceOnPosition(row, column);
			if(piece == null) {
				//return false;
				continue;
			}
			characteristics = piece.getCharacteristicsArray();
			for(int k = 0; k < commonCharacteristics.length; k++) {
				if(characteristics[k] == initialCharacteristics[k] && commonCharacteristics[k] != -1){
					commonCharacteristics[k]++;
				}
				else {
					commonCharacteristics[k] = -1;
				}

			}
		}

        Arrays.sort(commonCharacteristics);
		int returnVal = commonCharacteristics[commonCharacteristics.length - 1];
		return (returnVal == -1) ? 0 : returnVal;

	}
	protected int getNumberOfIdenticalCharacteristicsInHighDiagonal(QuartoBoard board) {
		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		boolean[] initialCharacteristics = null;
		int i, j;
		for(i = board.getNumberOfRows()-1, j = 0; i >= 0; i--, j++) {
			if (board.getPieceOnPosition(i,j) != null) {
				initialCharacteristics = board.getPieceOnPosition(i, j).getCharacteristicsArray();
				break;
			}
		}
		if (initialCharacteristics == null) {
			return 0;
		}


		for(int row = i, column = j; row >= 0; row--, column++) {
			QuartoPiece piece = board.getPieceOnPosition(row, column);
			if(piece == null) {
				//return false;
				continue;
			}
			characteristics = piece.getCharacteristicsArray();
			for(int k = 0; k < commonCharacteristics.length; k++) {
				if(characteristics[k] == initialCharacteristics[k] && commonCharacteristics[k] != -1){
						commonCharacteristics[k]++;
				}
				else {
					commonCharacteristics[k] = -1;
				}
			}

		}

        Arrays.sort(commonCharacteristics);
		int returnVal = commonCharacteristics[commonCharacteristics.length - 1];
		return (returnVal == -1) ? 0 : returnVal;
	}
}
