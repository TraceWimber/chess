package chess;

import java.util.Set;

public interface MoveCalculator {
    /**
     * Calculate all the moves the piece at
     * the given position can make
     *
     * @param board the board to search
     * @param myPosition the position of the piece
     * @return a set of possible moves
     */
    Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition);
}
