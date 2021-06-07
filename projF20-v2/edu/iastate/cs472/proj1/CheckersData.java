package edu.iastate.cs472.proj1;

import java.util.ArrayList;
import java.util.Arrays;

/**
    @author Ganesh Prasad
 */

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        this.board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(i%2 == j%2) {
                    if(i<3)
                        board[i][j] = BLACK;
                    else if(i>4)
                        board[i][j] = RED;
                }
                else
                    board[i][j] = EMPTY;
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     * @return  true if the piece becomes a king, otherwise false
     */
    boolean makeMove(CheckersMove move) {
        return makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     * @return        true if the piece becomes a king, otherwise false
     */
    boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        // 2. if this move is a jump, remove the captured piece
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        CheckersMove test = new CheckersMove(fromRow, fromCol, toRow, toCol);
        if(test.isJump()) {
            board[((fromRow + toRow)/2)][((fromCol) + toCol)/2] = EMPTY;
        }
        if(toRow == 0 && board[toRow][toCol] == RED) {
            board[toRow][toCol] = RED_KING;
            return true;
        }
        if(toRow == 7 && board[toRow][toCol] == BLACK) {
            board[toRow][toCol] = BLACK_KING;
            return true;
        }
        return false;
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        //check player
        if(player != RED && player != BLACK)
            return null;

        int king;
        if(player == RED)
            king = RED_KING;
        else
            king = BLACK_KING;

        ArrayList<CheckersMove> possibleMoves = new ArrayList<CheckersMove>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] == player || board[i][j] == king) {
                    if(canJump(player, i, j, i+1, j+1, i+2, j+2))
                        possibleMoves.add(new CheckersMove(i, j, i+2, j+2));
                    if(canJump(player, i, j, i-1, j-1, i-2, j-2))
                        possibleMoves.add(new CheckersMove(i, j, i-2, j-2));
                    if(canJump(player, i, j, i-1, j+1, i-2, j+2))
                        possibleMoves.add(new CheckersMove(i, j, i-2, j+2));
                    if(canJump(player, i, j, i+1, j-1, i+2, j-2))
                        possibleMoves.add(new CheckersMove(i, j, i+2, j-2));
                }
            }
        }

        if(possibleMoves.size() == 0) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if(board[i][j] == player || board[i][j] == king) {
                        if(canMove(player, i, j, i+1, j+1))
                            possibleMoves.add(new CheckersMove(i, j, i+1, j+1));
                        if(canMove(player, i, j, i-1, j-1))
                            possibleMoves.add(new CheckersMove(i, j, i-1, j-1));
                        if(canMove(player, i, j, i-1, j+1))
                            possibleMoves.add(new CheckersMove(i, j, i-1, j+1));
                        if(canMove(player, i, j, i+1, j-1))
                            possibleMoves.add(new CheckersMove(i, j, i+1, j-1));
                    }
                }
            }
        }

        if(possibleMoves.size()==0)
            return null;
        CheckersMove[] result = possibleMoves.toArray(new CheckersMove[possibleMoves.size()]);
        return result;
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        if(player != RED && player != BLACK)
            return null;

        int king;
        if(player == RED)
            king = RED_KING;
        else
            king = BLACK_KING;

        ArrayList<CheckersMove> possibleMoves = new ArrayList<CheckersMove>();
        int i=row;
        int j=col;

        if(board[i][j] == player || board[i][j] == king) {
            if(canJump(player, i, j, i+1, j+1, i+2, j+2))
                possibleMoves.add(new CheckersMove(i, j, i+2, j+2));
            if(canJump(player, i, j, i-1, j-1, i-2, j-2))
                possibleMoves.add(new CheckersMove(i, j, i-2, j-2));
            if(canJump(player, i, j, i-1, j+1, i-2, j+2))
                possibleMoves.add(new CheckersMove(i, j, i-2, j+2));
            if(canJump(player, i, j, i+1, j-1, i+2, j-2))
                possibleMoves.add(new CheckersMove(i, j, i+2, j-2));
        }

        if(possibleMoves.size()==0)
            return null;
        return possibleMoves.toArray(new CheckersMove[possibleMoves.size()]);
    }

    private boolean canMove(int player, int row1, int col1, int row2, int col2) {
        if(row2<0 || row2>=8 || col2<0 || col2>=8)
            return false;
        if(board[row2][col2] != EMPTY)
            return false;
        if(player == BLACK) {
            if(board[row1][col1] == BLACK && row2 < row1)
                return false;
            return true;
        }
        if(board[row1][col1] == RED && row2 > row1)
            return false;
        return true;
    }

    private boolean canJump(int player, int row1, int col1,  int row2, int col2, int row3, int col3) {
        if (row3<0 || row3>=8 || col3<0 || col3 >=8)
            return false;
        if(board[row3][col3] != EMPTY)
            return false;
        if(player == BLACK) {
            if(board[row1][col1] == BLACK && row3 < row1)
                return false;
            if(board[row2][col2] != RED && board[row2][col2] != RED_KING)
                return false;
            return true;
        }
        if(board[row1][col1] == RED && row3 > row1)
            return false;
        if(board[row2][col2] != BLACK && board[row2][col2] != BLACK_KING)
            return false;
        return true;
    }

}