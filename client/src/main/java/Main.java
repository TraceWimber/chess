import chess.*;
import server.BadFacadeRequestException;
import ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        //TODO: Remove these lines if the test cases pass
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Client: " + piece);

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new MainMenu(serverUrl).run();
    }
}