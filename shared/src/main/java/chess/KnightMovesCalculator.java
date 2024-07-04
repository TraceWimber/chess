package chess;

import java.util.HashSet;
import java.util.Set;

public class KnightMovesCalculator implements MoveCalculator {

    @Override
    public Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> possibleMoves = new HashSet<>();
        int row = myPosition.getRow() - 1;      // Convert to 0-based index
        int col = myPosition.getColumn() - 1;   // Convert to 0-based index

        // Directions the knight can move
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };

        // Check in each L-shaped direction
        for (int[] move : knightMoves) {
            int currRow = row + move[0];
            int currCol = col + move[1];

            // Add only if the move is on the board
            if (isValid(currRow, currCol)) {
                ChessPosition currPos = new ChessPosition(currRow + 1, currCol + 1);
                ChessPiece pieceAtPos = board.getPiece(currPos);
                // Add only if position is open or contains enemy
                if (pieceAtPos == null || !pieceAtPos.getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                    possibleMoves.add(new ChessMove(myPosition, currPos, null));
                }
            }
        }

        return possibleMoves;
    }

    // Check if a position is on the board (uses 0-based indexing)
    private boolean isValid(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
