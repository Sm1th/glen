import java.lang.*;
import java.util.Arrays;
public class QuartoPlayerAgent extends QuartoAgent {

    private final int DEPTH = 2;

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
        System.out.println("Choosing piece....");
        int pieceID=-1;//THIS SHOULD NEVER ACTUALLY BE USED
        int minScore=Integer.MAX_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int i=0;i<this.quartoBoard.getNumberOfPieces();i++){
            if (!this.quartoBoard.isPieceOnBoard(i)){
                QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
                int score = minimaxPosition(copyBoard, i, 0, false, alpha, beta);
                if (score<minScore){
                    pieceID = i;
                    minScore = score;
                }
            }
        }
        String BinaryString = String.format("%5s", Integer.toBinaryString(pieceID)).replace(' ', '0');
        return BinaryString;
    }

    private int minimaxPosition(QuartoBoard board, int pieceID, int depth, boolean isGlensTurn, int alpha, int beta){
        // System.out.println("minimaxPosition depth: " + depth);
        if (depth>DEPTH) {
            return getHeuristicValue(board);
        }
        int maxScore = Integer.MIN_VALUE;
        for (int row=0;row<board.getNumberOfRows();row++){
            for (int col=0;col<board.getNumberOfColumns();col++){
                QuartoBoard copyBoard = new QuartoBoard(board);
                if (!copyBoard.isSpaceTaken(row, col)){
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    if(checkIfGameIsWon(copyBoard)){
                        if (isGlensTurn){
                            return Integer.MAX_VALUE;
                        } else {
                            return Integer.MIN_VALUE;
                        }
                    }
                    int score = minimaxPiece(copyBoard, depth+1, isGlensTurn, alpha, beta);
                    if (score>maxScore){
                        maxScore = score;
                    } 
                    if (maxScore>=beta){
                        return maxScore;
                    }
                    if (maxScore>alpha){
                        alpha = maxScore;
                    }
                }
                
            }
        }
        return maxScore;
    }

    private int minimaxPiece(QuartoBoard board, int depth, boolean isGlensTurn, int alpha, int beta){
        // System.out.println("minimaxPiece depth: " + depth);
        if (depth>DEPTH) {
            return getHeuristicValue(board);
        }
        int minScore=Integer.MAX_VALUE;
        for (int i=0;i<this.quartoBoard.getNumberOfPieces();i++){
            if (!this.quartoBoard.isPieceOnBoard(i)){
                QuartoBoard copyBoard = new QuartoBoard(board);
                int score = minimaxPosition(copyBoard, i, depth+1, !isGlensTurn, alpha, beta);
                if (score<minScore){
                    minScore = score;
                }
                if (minScore<=alpha){
                    return minScore;
                }
                if (minScore<beta){
                    beta = minScore;
                }
            }
        }
        return minScore;
    }

    /*
     * Do Your work here
     * The server expects a move in the form of:   row,column
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        //do work
        System.out.println("Choosing move....");
        int[] move = new int[2];
    	int maxScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
    	for(int row = 0; row < this.quartoBoard.getNumberOfRows(); row++) {
    		for(int col = 0; col < this.quartoBoard.getNumberOfColumns(); col++) {
    			if(!this.quartoBoard.isSpaceTaken(row, col)) {
    				QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
    				copyBoard.insertPieceOnBoard(row, col, pieceID);
                    if(checkIfGameIsWon(copyBoard)){
                        return row + "," + col;
                    }
    				int score = minimaxPiece(copyBoard, 0, true, alpha, beta);
    				if (score > maxScore) {
    					maxScore = score;
    					move[0] = row;
    					move[1] = col;
    				}
                    if (maxScore>=beta){
                        return move[0] + "," + move[1];
                    }
                    if (maxScore>alpha){
                        alpha = maxScore;
                    }
    			}
    		}
    	}

        return move[0] + "," + move[1];
    }

    private double monteCarlo(QuartoBoard board, int simulations, int turn){
        if (simulations<=0) {
            simulations=100;
        }
        double sum = 0;
        for (int i=0;i<simulations;i++){
            QuartoBoard copyBoard = new QuartoBoard(board);
            while (!copyBoard.checkIfBoardIsFull()){
                int pieceId = copyBoard.chooseRandomPieceNotPlayed(100);
                int[] move = copyBoard.chooseRandomPositionNotPlayed(100);
                copyBoard.insertPieceOnBoard(move[0], move[1], pieceId);
                if (checkIfGameIsWon(copyBoard)){
                    sum+=turn;
                    break;
                }
                turn = 0-turn;
            }
        }
        return sum/simulations;
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

	    return 4*count4 + 3*count3 + 2*count2 + count1;

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
