package chess;

import java.util.HashSet;
import java.util.Set;

public class QueenMovesCalculator implements MoveCalculator {

    @Override
    public Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> possibleMoves = new HashSet<>();
        int row = myPosition.getRow() - 1;      // Convert to 0-based index
        int col = myPosition.getColumn() - 1;   // Convert to 0-based index

        // Directions the queen can move
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        // Check in each direction
        for (int[] move : directions) {
            int currRow = row + move[0];
            int currCol = col + move[1];

            // Move along each direction until reaching another piece or edge of board
            while (isValid(currRow, currCol)) {
                ChessPosition currPos = new ChessPosition(currRow + 1, currCol + 1);
                ChessPiece pieceAtPos = board.getPiece(currPos);
                if (pieceAtPos == null) {
                    possibleMoves.add(new ChessMove(myPosition, currPos, null));
                } else {
                    // If there's a piece here, check it's color and add position if enemy, then break
                    if (!pieceAtPos.getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                        possibleMoves.add(new ChessMove(myPosition, currPos, null));
                    }
                    break;
                }
                currRow += move[0];
                currCol += move[1];
            }
        }

        return possibleMoves;
    }

    /**
     * Helper function. Check if a position is
     * on the board
     *
     * @param row row index (0-based)
     * @param col column index (0-based)
     * @return true if on the board
     */
    private boolean isValid(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
