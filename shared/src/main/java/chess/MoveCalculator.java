package chess;

import java.util.Set;

public interface MoveCalculator {
    public Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition);
}
