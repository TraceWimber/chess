package chess;

import java.util.HashSet;
import java.util.Set;

public class RookMovesCalculator implements MoveCalculator {

    @Override
    public Set<ChessMove> calcMoves(ChessBoard board, ChessPosition myPosition) {
        return new HashSet<>();
    }
}