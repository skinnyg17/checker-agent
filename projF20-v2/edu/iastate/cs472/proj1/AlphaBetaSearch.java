package edu.iastate.cs472.proj1;

/**
 @author Ganesh Prasad
 */

public class AlphaBetaSearch {
    private CheckersData board;

    /**
     *  A class to hold a pair of a move and an evaluation value,
     *  as specified in the psuedocode found in lecture12 slides.
     */
    public class Pair {
        public int _value;
        public CheckersMove _move;

        public Pair() {
            _move = null;
            _value = -1;
        }

        public Pair(CheckersMove move, int value) {
            this._move = move;
            this._value = value;
        }
    }

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    public void setCheckersData(CheckersData board) {
        this.board = board;
    }

    // Todo: You can implement your helper methods here

    /**
     *  You need to implement the Alpha-Beta pruning algorithm here to
     * find the best move at current stage.
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();
        return maxValue(this.board, -99999, 99999, CheckersData.BLACK, 0)._move;
    }

    Pair maxValue(CheckersData board, int alpha, int beta, int player, int depth) {
        if(board.getLegalMoves(player) == null || depth > 12)
            return new Pair(null,evaluator1(board));
        CheckersMove[] nextMoves = board.getLegalMoves(player);
        Pair best = new Pair();
        for(CheckersMove move : nextMoves) {
            int rank = minValue(getNewBoard(board, move), alpha, beta, (player == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED, depth+1)._value;
            if(checkDoubleJump(move,board, player)){
                if(player == CheckersData.RED || player == CheckersData.BLACK_KING)
                    rank -= 5;
                else
                    rank += 5;
            }
            if(rank > alpha) {
                best = new Pair(move, rank);
                alpha = Math.max(alpha, rank);
            }
            if(alpha >= beta)
                return best;
        }
        return best;
    }

    Pair minValue(CheckersData board, int alpha, int beta, int player, int depth) {
        if(board.getLegalMoves(player) == null || depth > 12)
            return new Pair(null, evaluator1(board));
        CheckersMove[] nextMoves = board.getLegalMoves(player);
        Pair best = new Pair();
        for(CheckersMove move : nextMoves) {
            int rank = maxValue(getNewBoard(board, move), alpha, beta, (player == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED, depth+1)._value;
            if(checkDoubleJump(move,board, player)){
                if(player == CheckersData.RED || player == CheckersData.BLACK_KING)
                    rank -= 5;
                else
                    rank += 5;
            }
            if(rank < beta) {
                best = new Pair(move, rank);
                beta = Math.min(beta, rank);
            }
            if(beta <= alpha)
                return best;
        }
        return best;
    }

    int evaluator(CheckersData board) {
        int noBlack = 0;
        int noRed = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board.pieceAt(i,j) == CheckersData.BLACK)
                    noBlack +=i;
                else if(board.pieceAt(i,j) == CheckersData.BLACK_KING)
                    noBlack = noBlack + i +2;
                else if(board.pieceAt(i,j) == CheckersData.RED)
                    noRed += i;
                else if(board.pieceAt(i,j) == CheckersData.RED_KING)
                    noRed = noRed + i + 2;
            }
        }
        return noBlack - noRed;
    }

    int evaluator1(CheckersData board) {
        int noBlack = 0;
        int noRed = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board.pieceAt(i,j) == CheckersData.BLACK)
                    noBlack = (8-i);
                else if(board.pieceAt(i,j) == CheckersData.BLACK_KING)
                    noBlack += (8-i) + 2;
                else if(board.pieceAt(i,j) == CheckersData.RED)
                    noRed += i;
                else if(board.pieceAt(i,j) == CheckersData.RED_KING)
                    noRed += (i+2);
            }
        }
        return noBlack - noRed;
    }

    CheckersData getNewBoard(CheckersData curr, CheckersMove move) {
        CheckersData result = new CheckersData();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                result.board[i][j] = curr.pieceAt(i,j);
            }
        }
        result.makeMove(move);
        return result;
    }

    boolean checkDoubleJump(CheckersMove move, CheckersData board, int player) {
        if(move.isJump()) {
            CheckersData temp = getNewBoard(board, move);
            CheckersMove[] check = temp.getLegalMoves(player);
            if(check != null) {
                if(check[0].isJump())
                    return true;
            }
            return false;
        }
        return false;
    }
}
