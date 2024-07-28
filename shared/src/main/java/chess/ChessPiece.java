package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private boolean hasMoved;
    private boolean justMovedTwo;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        hasMoved = false;
        justMovedTwo = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * @return true if the piece has moved this game
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * marks the piece as having moved
     */
    public void setHasMoved() {
        hasMoved = true;
    }

    /**
     * updates whether this piece just moved two spaces (pawns only)
     */
    public void setJustMovedTwo(boolean hasMovedTwo) {
        justMovedTwo = hasMovedTwo;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalculator moves;
        return (switch (type) {
            case KING -> moves = new KingMovesCalculator();
            case QUEEN -> moves = new QueenMovesCalculator();
            case BISHOP -> moves = new BishopMovesCalculator();
            case KNIGHT -> moves = new KnightMovesCalculator();
            case ROOK -> moves = new RookMovesCalculator();
            case PAWN -> moves = new PawnMovesCalculator();
            default -> throw new RuntimeException("Invalid PieceType");
        }).calcMoves(board, myPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof ChessPiece that)) {return false;}
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor == ChessGame.TeamColor.WHITE ? type.name().substring(0, 1) : type.name().substring(0, 1).toLowerCase();
    }
}
