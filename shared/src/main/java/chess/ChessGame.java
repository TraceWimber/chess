package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard;
    private ChessMove lastMove;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        lastMove = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or empty collection if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        // If there is no piece, there are no moves
        if (piece == null) {
            return new HashSet<>();
        }

        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);
        Set<ChessMove> validMoveSet = new HashSet<>();
        // Check if each move will result in check
        for (ChessMove move : moves) {
            // Temporarily make the move on the board
            ChessPiece targetPiece = gameBoard.getPiece(move.getEndPosition());
            gameBoard.addPiece(move.getEndPosition(), piece);
            gameBoard.addPiece(startPosition, null);
            // If move doesn't result in check for the moving piece's team, it is a valid move
            if (!isInCheck(piece.getTeamColor())) {
                validMoveSet.add(move);
            }
            // Revert the move
            gameBoard.addPiece(startPosition, piece);
            gameBoard.addPiece(move.getEndPosition(), targetPiece);
        }

        //TODO: Add in special move sets here!!
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            validMoveSet.addAll(getCastlingMoves(startPosition, piece));
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            validMoveSet.addAll(getEnPassantMoves(startPosition, piece));
        }

        return validMoveSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Check that move is valid and it's that piece's turn
        if (!validMoves(move.getStartPosition()).contains(move) || gameBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Invalid move!");
        }
        // Make the move
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        //TODO: Add in logic to make special moves happen here!!
        // make sure to account for if pieces have moved already.
        if (isCastlingMove(move, piece)) {
            makeCastlingMove(move);
        }
        else if (isEnPassantMove(move, piece)) {
            makeEnPassantMove(move);
        }
        else {
            // If there is a promotion, make it happen
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(gameBoard.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
            }
            gameBoard.addPiece(move.getEndPosition(), piece);
            gameBoard.addPiece(move.getStartPosition(), null);
        }

        // Set movement flags and save last move
        piece.setHasMoved();
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            piece.setJustMovedTwo(Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2);
        }
        lastMove = move;

        // Switch turn
        switch (teamTurn) {
            case WHITE -> teamTurn = TeamColor.BLACK;
            case BLACK -> teamTurn = TeamColor.WHITE;
            default -> throw new RuntimeException("Switching teams broke");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(teamColor);
        // Loop through each piece
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i + 1, j + 1));
                // If piece is enemy to the team whose turn it is
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, new ChessPosition(i + 1, j + 1));
                    // Check its move set, if any moves match the current king's position, it is in check
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If not in check, then cannot be in checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }
        // Check each piece
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition currPos = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = gameBoard.getPiece(currPos);
                // If it's the piece's turn, check its move set
                if (piece != null && piece.getTeamColor() == teamTurn) {
                    Collection<ChessMove> moves = validMoves(currPos);
                    // If there are valid moves, then it cannot be checkmate
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If in check or not your turn, cannot be stalemate
        if (isInCheck(teamColor) || teamColor != teamTurn) {
            return false;
        }
        // Check each piece
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition currPos = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = gameBoard.getPiece(currPos);
                // If it's the piece's turn, check its move set
                if (piece != null && piece.getTeamColor() == teamTurn) {
                    Collection<ChessMove> moves = validMoves(currPos);
                    // If there are valid moves, then it cannot be stalemate
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    /**
     * Helper function. Locates the King's position
     * on the board
     *
     * @param teamColor which team color's king to find
     * @return the king's position of the given color
     */
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i + 1, j + 1));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i + 1, j + 1);
                }
            }
        }
        return null;
    }

    /**
     * Helper function. Determines if the given
     * move is a castle move
     *
     * @param move the move being checked
     * @param piece the piece performing the move
     * @return true if the move is castling
     */
    private boolean isCastlingMove(ChessMove move, ChessPiece piece) {
        // If the piece isn't a king, it isn't castling
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }
        // Find differences between start and end positions to determine if castling
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        int rowDiff = Math.abs(start.getRow() - end.getRow());
        int colDiff = Math.abs(start.getColumn() - end.getColumn());
        return rowDiff == 0 && colDiff == 2;
    }

    /**
     * Helper function. Performs the given
     * castling move
     *
     * @param move the move being performed
     */
    private void makeCastlingMove(ChessMove move) {
        ChessPiece king = gameBoard.getPiece(move.getStartPosition());
        int row = move.getStartPosition().getRow();

        // Decide if King or Queen-side castling
        if (move.getEndPosition().getColumn() == 7) { // King-side castling
            ChessPiece rook = gameBoard.getPiece(new ChessPosition(row, 8));
            gameBoard.addPiece(new ChessPosition(row, 7), king);
            gameBoard.addPiece(new ChessPosition(row, 6), rook);
            gameBoard.addPiece(new ChessPosition(row, 5), null);
            gameBoard.addPiece(new ChessPosition(row, 8), null);
        }
        else if (move.getEndPosition().getColumn() == 3) {
            ChessPiece rook = gameBoard.getPiece(new ChessPosition(row, 1));
            gameBoard.addPiece(new ChessPosition(row, 3), king);
            gameBoard.addPiece(new ChessPosition(row, 4), rook);
            gameBoard.addPiece(new ChessPosition(row, 5), null);
            gameBoard.addPiece(new ChessPosition(row, 1), null);
        }
    }

    //helper function to get castling moves
    private Collection<ChessMove> getCastlingMoves(ChessPosition position, ChessPiece king) {
        Set<ChessMove> moves = new HashSet<>();
        if (king.hasMoved() || isInCheck(king.getTeamColor())) {
            return moves;
        }

        if (canCastleKingside(position.getRow(), king.getTeamColor())) {
            moves.add(new ChessMove(position, new ChessPosition(position.getRow(), 7), null));
        }
        if (canCastleQueenside(position.getRow(), king.getTeamColor())) {
            moves.add(new ChessMove(position, new ChessPosition(position.getRow(), 3), null));
        }

        return moves;
    }

    //helper function to determine if king-side castling is possible; takes in 1-based row
    private boolean canCastleKingside(int row, TeamColor team) {
        ChessPiece piece = gameBoard.getPiece(new ChessPosition(row, 8));
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            ChessPosition pos1 = new ChessPosition(row, 7);
            ChessPosition pos2 = new ChessPosition(row, 6);
            return !piece.hasMoved() &&
                    gameBoard.getPiece(pos1) == null &&
                    gameBoard.getPiece(pos2) == null &&
                    isSafe(pos1, team) &&
                    isSafe(pos2, team);
        }
        return false;
    }

    //helper function to determine if queen-side castling is possible; takes in 1-based row
    private boolean canCastleQueenside(int row, TeamColor team) {
        ChessPiece piece = gameBoard.getPiece(new ChessPosition(row, 1));
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            ChessPosition pos1 = new ChessPosition(row, 4);
            ChessPosition pos2 = new ChessPosition(row, 3);
            ChessPosition pos3 = new ChessPosition(row, 2);
            return !piece.hasMoved() &&
                    gameBoard.getPiece(pos1) == null &&
                    gameBoard.getPiece(pos2) == null &&
                    gameBoard.getPiece(pos3) == null &&
                    isSafe(pos1, team) &&
                    isSafe(pos2, team);
        }
        return false;
    }

    //helper function to check if a space is safe
    private boolean isSafe(ChessPosition targetPos, TeamColor team) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i + 1, j + 1));
                if (piece != null && piece.getTeamColor() != team) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, new ChessPosition(i + 1, j + 1));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(targetPos)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isEnPassantMove(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return false;
        }
        return move.getStartPosition().getColumn() != move.getEndPosition().getColumn() && gameBoard.getPiece(move.getEndPosition()) == null;
    }

    private void makeEnPassantMove(ChessMove move) {
        ChessPiece pawn = gameBoard.getPiece(move.getStartPosition());
        gameBoard.addPiece(move.getEndPosition(), pawn);
        gameBoard.addPiece(move.getStartPosition(), null);
        gameBoard.addPiece(new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn()), null);
    }

    private Collection<ChessMove> getEnPassantMoves(ChessPosition position, ChessPiece pawn) {
        Set<ChessMove> moves = new HashSet<>();
        int col = position.getColumn();
        int moveDir = pawn.getTeamColor() == TeamColor.WHITE ? 1 : -1;

        if (lastMove == null) {
            return moves;
        }

        ChessPiece lastMovedPiece = gameBoard.getPiece(lastMove.getEndPosition());
        if (lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(lastMove.getStartPosition().getRow() - lastMove.getEndPosition().getRow()) == 2) {
            if (lastMove.getEndPosition().getColumn() == col - 1 || lastMove.getEndPosition().getColumn() == col + 1) {
                moves.add(new ChessMove(position, new ChessPosition(position.getRow() + moveDir, lastMove.getEndPosition().getColumn()), null));
            }
        }

        return moves;
    }
}
