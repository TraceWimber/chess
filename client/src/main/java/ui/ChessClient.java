package ui;

import server.BadFacadeRequestException;
import server.ServerFacade;
import java.util.Arrays;

public class ChessClient {

    private final ServerFacade server;
    private final String serverUrl;
    private boolean isSignedIn = false;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        if (!isSignedIn) {
            try {
                var tokens = input.toLowerCase().split(" ");
                var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                return switch (cmd) {
                    case "Quit" -> "quit";
                    case "Login" -> login(params);
                    case "Register" -> register(params);
                    default -> help();
                };
            } catch (BadFacadeRequestException ex) {
                return ex.getMessage();
            }
        }

        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "Logout" -> logout(params);
                case "Create" -> createGame(params);
                case "List" -> listGames();
                case "Play" -> joinGame();
                case "Observe" -> observeGame(params);
                default -> help();
            };
        } catch (BadFacadeRequestException ex) {
            return ex.getMessage();
        }
    }




}
