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
                    case "quit" -> "quit";
                    case "login" -> login(params);
                    case "register" -> register(params);
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
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> joinGame();
                case "observe" -> observeGame(params);
                default -> help();
            };
        } catch (BadFacadeRequestException ex) {
            return ex.getMessage();
        }
    }

    public String login(String[] params) throws BadFacadeRequestException {
        isSignedIn = true;
        throw new BadFacadeRequestException(400, "login not done!");
    }

    public String register(String[] params) throws BadFacadeRequestException {
        throw new BadFacadeRequestException(400, "register not done!");
    }

    public String help() {
        if (isSignedIn) {
            return """
                    - logout
                    - list
                    - create
                    - play
                    - observe
                    """;
        }
        return """
                - quit
                - login
                - register
                """;
    }

    public String logout(String[] params) throws BadFacadeRequestException {
        isSignedIn = false;
        System.out.println("logout not done!");
        return "Logged out!";
    }

    public String createGame(String[] params) throws BadFacadeRequestException {
        throw new BadFacadeRequestException(400, "createGame not done!");
    }

    public String listGames() throws BadFacadeRequestException {
        throw new BadFacadeRequestException(400, "listGames not done!");
    }

    public String joinGame() throws BadFacadeRequestException {
        throw new BadFacadeRequestException(400, "joinGame not done!");
    }

    public String observeGame(String[] params) throws BadFacadeRequestException {
        throw new BadFacadeRequestException(400, "observeGame not done!");
    }
}
