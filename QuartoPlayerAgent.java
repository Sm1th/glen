import java.lang.*;
import java.util.Arrays;
public class QuartoPlayerAgent extends QuartoAgent {

    private int maxDepth = 4;
    private final int WIN_SCORE = 100;
    private final int LOSE_SCORE = -100;
    private final int TIE_SCORE = 0;
    private final int COMPUTATIONAL_DELAY = COMMUNICATION_DELAY + 250;

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
        this.startTimer();
        int pieceID=-1;//THIS SHOULD NEVER ACTUALLY BE USED
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int value = Integer.MIN_VALUE;
        for (int i=0;i<this.quartoBoard.getNumberOfPieces();i++){
            QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
            if (!copyBoard.isPieceOnBoard(i)){
                int successorValue = minChoosePosition(copyBoard, i, alpha, beta, 1);
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
            if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                break;
            }
        }
        String BinaryString = String.format("%5s", Integer.toBinaryString(pieceID)).replace(' ', '0');
        return BinaryString;
    }

    private int minChoosePosition(QuartoBoard board, int pieceID, int alpha, int beta, int depth){
        if (depth>=maxDepth){
            return -getHeuristicValue(board);
        }
        int value = Integer.MAX_VALUE;
        for (int row=0;row<board.getNumberOfRows();row++){
            for (int col=0;col<board.getNumberOfColumns();col++){
                if (!board.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(board);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    int successorValue;
                    if (checkIfGameIsWon(copyBoard)){
                        return LOSE_SCORE;
                    }
                    else if (copyBoard.checkIfBoardIsFull()){
                        return TIE_SCORE;
                    }
                    else {
                        successorValue = minChoosePiece(copyBoard, alpha, beta, depth+1);
                    }

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
                if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                    return value;
                }   
            }
        }
        return value;
    }

    private int minChoosePiece(QuartoBoard board, int alpha, int beta, int depth){
        if (depth>=maxDepth){
            return -getHeuristicValue(board);
        }
        int value = Integer.MAX_VALUE;
        for (int i=0;i<board.getNumberOfPieces();i++){
            QuartoBoard copyBoard = new QuartoBoard(board);
            if (!copyBoard.isPieceOnBoard(i)){
                int successorValue = maxChoosePosition(copyBoard, i, alpha, beta, depth+1);
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
            if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                return value;
            } 
        }
        return value;
    }

    private int maxChoosePosition(QuartoBoard board, int pieceID, int alpha, int beta, int depth){
        if (depth>=maxDepth){
            return getHeuristicValue(board);
        }
        int value = Integer.MIN_VALUE;
        for (int row=0;row<board.getNumberOfRows();row++){
            for (int col=0;col<board.getNumberOfColumns();col++){
                if (!board.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(board);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    int successorValue;
                    if (checkIfGameIsWon(copyBoard)){
                        return WIN_SCORE;
                    }
                    else if (copyBoard.checkIfBoardIsFull()){
                        return TIE_SCORE;
                    }
                    else {
                        successorValue = maxChoosePiece(copyBoard, alpha, beta, depth+1);
                    }
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
                if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                    return value;
                } 
            }
        }
        return value;
    }

    private int maxChoosePiece(QuartoBoard board, int alpha, int beta, int depth){
        if (depth>=maxDepth){
            return getHeuristicValue(board);
        }
        int value = Integer.MIN_VALUE;
        for (int i=0;i<board.getNumberOfPieces();i++){
            if (!board.isPieceOnBoard(i)){
                QuartoBoard copyBoard = new QuartoBoard(board);
                int successorValue = minChoosePosition(copyBoard, i, alpha, beta, depth+1);
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
            if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                return value;
            } 
        }
        return value;
    }

    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        this.startTimer();
        int[] move = new int[2];
        int value = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int row=0;row<this.quartoBoard.getNumberOfRows();row++){
            for (int col=0;col<this.quartoBoard.getNumberOfColumns();col++){
                if (!this.quartoBoard.isSpaceTaken(row, col)){
                    QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
                    copyBoard.insertPieceOnBoard(row, col, pieceID);
                    int successorValue;
                    if (checkIfGameIsWon(copyBoard) || copyBoard.checkIfBoardIsFull()){
                        return row + "," + col;
                    }
                    else {
                        successorValue = maxChoosePiece(copyBoard, alpha, beta, 1);
                    }
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
                if (this.getMillisecondsFromTimer() > (this.timeLimitForResponse - COMPUTATIONAL_DELAY)) {
                    return move[0] + "," + move[1];
                } 
            }
        }
        return move[0] + "," + move[1];
    }

    private double monteCarlo(QuartoBoard board, boolean isGlensTurn, boolean isChoosePiece, int pieceId){
        int sum = 0;
        int simulations = 100;
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
        return TIE_SCORE;
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

	    return (4*count4) + (3*count3) + (2*count2) + (1*count1);

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

    private int piecesLeft(QuartoBoard board){
        int count = 0;
        for (int i=0;i<board.getNumberOfPieces();i++){
            if (!board.isPieceOnBoard(i)){
                count++;
            }
        }
        return count;
    }

    private int spacesLeft(QuartoBoard board){
        int count = 0;
        for (int i=0;i<board.getNumberOfRows();i++){
            for (int j=0;j<board.getNumberOfColumns();j++){
                if (!board.isSpaceTaken(i,j)){
                    count++;
                }
            }
        }
        return count;
    }

    private void decideDepth(){
        int spacesLeft = spacesLeft(this.quartoBoard);
        int piecesLeft = spacesLeft+7;
        maxDepth = 0;
        int nodes = 32*25*31*24; //first 4 turns in the game...
        while (nodes>10){
            if (maxDepth%2==0) {
                nodes = nodes/spacesLeft;
                spacesLeft--;
            }else{
                nodes = nodes/piecesLeft;
                piecesLeft--;
            }
            if (spacesLeft==0 || piecesLeft==0){
                break;
            }
            maxDepth++;
        }
        System.out.println(maxDepth);
    }
}
