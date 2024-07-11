package chess;

import java.util.HashSet;
import java.util.Set;

public class PawnMovesCalculator implements MoveCalculator {

    @Override
    public Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> possibleMoves = new HashSet<>();
        int row = myPosition.getRow() - 1;      // Convert to 0-based indexing
        int col = myPosition.getColumn() - 1;   // Convert to 0-based indexing

        ChessPiece pawn = board.getPiece(myPosition);
        boolean isWhite = pawn.getTeamColor() == ChessGame.TeamColor.WHITE;

        int forwardMove = isWhite ? 1 : -1;
        int startRow = isWhite ? 1 : 6;
        int promotionRow = isWhite ? 7 : 0;

        // Move one square forward if on board and location empty
        if (isValid(row + forwardMove, col) && board.getPiece(new ChessPosition(row + forwardMove + 1, col + 1)) == null) {
            // If moving into promotion row, add all possible promotions
            if (row + forwardMove == promotionRow) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + forwardMove + 1, col + 1), ChessPiece.PieceType.QUEEN));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + forwardMove + 1, col + 1), ChessPiece.PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + forwardMove + 1, col + 1), ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + forwardMove + 1, col + 1), ChessPiece.PieceType.ROOK));
            }
            else {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + forwardMove + 1, col + 1), null));
            }

            // Move two squares forward if from start position and location empty
            if (row == startRow && board.getPiece(new ChessPosition(row + 2 * forwardMove + 1, col + 1)) == null) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2 * forwardMove + 1, col + 1), null));
            }
        }

        // Capture diagonally if forward diagonal is on the board and contains enemy
        int[][] captureMoves = {{forwardMove, -1}, {forwardMove, 1}};
        for (int[] move : captureMoves) {
            int currRow = row + move[0];
            int currCol = col + move[1];
            if (isValid(currRow, currCol)) {
                ChessPiece piece = board.getPiece(new ChessPosition(currRow + 1, currCol + 1));
                if (piece != null && piece.getTeamColor() != pawn.getTeamColor()) {
                    // If moving into promotion row, add all possible promotions
                    if (currRow == promotionRow) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currRow + 1, currCol + 1), ChessPiece.PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currRow + 1, currCol + 1), ChessPiece.PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currRow + 1, currCol + 1), ChessPiece.PieceType.BISHOP));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currRow + 1, currCol + 1), ChessPiece.PieceType.ROOK));
                    }
                    else {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currRow + 1, currCol + 1), null));
                    }
                }
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
