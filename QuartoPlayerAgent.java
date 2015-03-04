import java.util.Arrays;
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
	int bestHeuristicVal = -1;
	for(int row = 0; row < this.quartoBoard.getNumberOfRows(); row++) {
		for(int col = 0; col < this.quartoBoard.getNumberOfColumns(); col++) {
			if(this.quartoBoard.getPieceOnPosition(row, col) == null) {
				QuartoBoard copyBoard = new QuartoBoard(this.quartoBoard);
				copyBoard.insertPieceOnBoard(row, col, pieceID);
				int tempHeuristicVal = getHeuristicValue(copyBoard);
				if(tempHeuristicVal > bestHeuristicVal) {
					bestHeuristicVal = tempHeuristicVal;
					move[0] = row;
					move[1] = col;
				}
			}
		}
	}

        return move[0] + "," + move[1];
    }

    protected int getHeuristicValue(QuartoBoard board) {
	return 4*getNumIdenticalProps(board, 4) + 3*getNumIdenticalProps(board, 3) + 2*getNumIdenticalProps(board, 2) + 1*getNumIdenticalProps(board, 1);

    }

    protected int getNumIdenticalProps(QuartoBoard board, int numIdenticalProps) {
	int count1 = 0, count2 = 0, count3 = 0, count4 = 0;
	for(int i = 0; i < board.getNumberOfRows(); i++) {
		int nCol = checkColumn(board, i);
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

		int nRow = checkRow(board, i);
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


	}
	//TODO: *finish for diagonals*

	return 0;
		
    }


	protected int checkColumn(QuartoBoard board, int column) {

		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		//TODO: *might not have been initialized*
		boolean[] initialCharacteristics;
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

		return commonCharacteristics[commonCharacteristics.length - 1];
	}

	protected int checkRow(QuartoBoard board, int row) {

		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		//TODO: *might not have been initialized*
		boolean[] initialCharacteristics;
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

		return commonCharacteristics[commonCharacteristics.length - 1];

	}

	protected int checkLowDiagonal(QuartoBoard board) {
		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		//TODO: *might not have been initialized*
		boolean[] initialCharacteristics;
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

		return commonCharacteristics[commonCharacteristics.length - 1];

		}
	protected int checkHighDiagonal(QuartoBoard board) {
		boolean[] characteristics;
		int[] commonCharacteristics = new int[] {0, 0, 0, 0, 0};
		//TODO: *might not have been initialized*
		boolean[] initialCharacteristics;
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

		return commonCharacteristics[commonCharacteristics.length - 1];
	}

}
