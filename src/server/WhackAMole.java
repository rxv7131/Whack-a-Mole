package server;

public class WhackAMole {
    public static final int ROWS = 3;
    public static final int COLUMNS = 4;
    private int rows;
    private int columns;
    private int[][] board;

    /**
     * Sets the rows and board to be 3x4
     * @param rows number of rows
     * @param columns number of columns
     */
    public WhackAMole(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        board = new int[columns][rows];
        for(int col=0; col<columns; col++) {
            for(int row=0; row < rows; row++) {
                board[col][row] = 0;
            }
        }
    }
}
