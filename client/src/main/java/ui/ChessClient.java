package ui;

import model.AuthData;
import model.UserData;
import server.BadFacadeRequestException;
import server.ServerFacade;
import java.util.Arrays;
import java.util.Objects;

public class ChessClient {

    private final ServerFacade server;
    private final String serverUrl;
    private boolean isSignedIn = false;
    private AuthData currAuth;
    private UserData currUser;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        currAuth = null;
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
        if (params.length == 2) {
            currUser = new UserData(params[0], params[1], null);
            //TODO: Uncomment this when you're ready to test this command with the server
            // and delete the following if statement block.
            //currAuth = server.login(currUser);

            if (Objects.equals(params[1], "correctpass")) {
                currAuth = new AuthData("authTokenHere", params[0]);
            }
            if (currAuth != null) {
                isSignedIn = true;
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged in!";
            }
            throw new BadFacadeRequestException(400, "username/password incorrect!");
        }
        throw new BadFacadeRequestException(400, "Expected: login <username> <password>");
    }

    public String register(String[] params) throws BadFacadeRequestException {
        if (params.length == 3) {
            currUser = new UserData(params[0], params[1], params[2]);
            //TODO: Uncomment this when you're ready to test this command with the server
            // and delete the following if statement block.
            //currAuth = server.register(currUser);

            if (!Objects.equals(params[0], "taken")) {
                currAuth = new AuthData("authTokenHere", params[0]);
            }

            if (currAuth != null) {
                isSignedIn = true;
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged in!";
            }
            else {
                throw new BadFacadeRequestException(400, "That username is taken, sorry.");
            }
            //TODO: Will currAuth ever be null without throwing an error?
        }
        throw new BadFacadeRequestException(400, "Expected: register <username> <password> <email>");
    }

    public String help() {
        System.out.println( EscapeSequences.SET_TEXT_COLOR_GREEN + "Type in one of the commands below!" + EscapeSequences.RESET_TEXT_COLOR);
        if (isSignedIn) {
            return """
                    • logout - sign out
                    • list - list all of the available games
                    • create - create a new game
                    • play - join a game
                    • observe - spectate a game
                    """;
        }
        return """
                • quit
                • login - to get started
                • register - if you are new here!
                """;
    }

    public String logout(String[] params) throws BadFacadeRequestException {
        isSignedIn = false;
        currAuth = null;
        System.out.println("logout not done!");
        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged out!";
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
