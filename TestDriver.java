import java.util.Arrays;

class TestDriver {

	public static void main(String[] args) {
		QuartoBoard board;

		for(int i = 0; i < 12; i++) {
			board =new QuartoBoard(5, 5, 32, "/Users/Joshua/Google Drive/University/3/WI2015/CS4725/Quarto/glen/test_cases/state" + i + ".quarto");
			System.out.println("Heuristic val for state" + i + ".quarto: " + getHeuristicValue(board));
		}

		// for (int i = 0; i < board.getNumberOfColumns(); i++) {
		// 	System.out.println("Col#" + i+ ": " + getNumberOfIdenticalCharacteristicsInColumn(board,i));
		// }
		//
		// for (int i = 0; i < board.getNumberOfColumns(); i++) {
		// 	System.out.println("Row#" + i+ ": " + getNumberOfIdenticalCharacteristicsInRow(board,i));
		// }
		//
		// System.out.println("Low diag: " + getNumberOfIdenticalCharacteristicsInLowDiagonal(board));
		//
		// System.out.println("High diag: " + getNumberOfIdenticalCharacteristicsInHighDiagonal(board));


	}

	protected static int getHeuristicValue(QuartoBoard board) {
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

	protected static int getNumberOfIdenticalCharacteristicsInColumn(QuartoBoard board, int column) {

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

	protected static int getNumberOfIdenticalCharacteristicsInRow(QuartoBoard board, int row) {

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

	protected static int getNumberOfIdenticalCharacteristicsInLowDiagonal(QuartoBoard board) {
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
	protected static int getNumberOfIdenticalCharacteristicsInHighDiagonal(QuartoBoard board) {
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
