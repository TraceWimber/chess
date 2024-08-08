package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.BadFacadeRequestException;
import server.BadInputException;
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
        try {
            if (!isSignedIn) {
                var tokens = input.toLowerCase().split(" ");
                var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                return switch (cmd) {
                    case "quit" -> "quit";
                    case "login" -> login(params);
                    case "register" -> register(params);
                    default -> help();
                };
            }

            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> joinGame(params);
                case "observe" -> observeGame(params);
                default -> help();
            };
        } catch (BadFacadeRequestException ex) {
            return switch (ex.getStatusCode()) {
                case 400 -> "Error: Unexpected Input!";
                case 401 -> "Error: Unauthorized. Please login, or enter correct username/password.";
                case 403 -> "Error: That username or game slot is taken, sorry!";
                default -> "Server error, please try again.";
            };
        } catch (BadInputException ex) {
            return ex.getMessage();
        }
    }

    public String login(String[] params) throws BadInputException, BadFacadeRequestException {
        if (params.length == 2) {
            currUser = new UserData(params[0], params[1], null);
            currAuth = server.login(currUser);

            if (currAuth != null) {
                isSignedIn = true;
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged in!";
            }
            throw new BadInputException(401, "username/password incorrect!");
        }
        throw new BadInputException(400, "Expected: login <username> <password>");
    }

    public String register(String[] params) throws BadFacadeRequestException, BadInputException {
        if (params.length == 3) {
            currUser = new UserData(params[0], params[1], params[2]);
            currAuth = server.register(currUser);

            if (currAuth != null) {
                isSignedIn = true;
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged in!";
            }
            else {
                throw new BadInputException(403, "That username is taken, sorry.");
            }
        }
        throw new BadInputException(400, "Expected: register <username> <password> <email>");
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

    public String logout(String[] params) throws BadFacadeRequestException, BadInputException {
        server.logout(currAuth.authToken());
        isSignedIn = false;
        currAuth = null;

        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged out!";
    }

    public String createGame(String[] params) throws BadFacadeRequestException, BadInputException {
        if (params.length == 1) {
            GameData game = server.createGame(currAuth.authToken(), new GameData(0, null, null, params[0], new ChessGame()));

            if (game != null) {
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Game Created!"
                        + EscapeSequences.SET_TEXT_COLOR_GREEN + " Use command 'list' to view game details.";
            }
            throw new BadFacadeRequestException(500, "Server failed to create game, try again.");
        }
        throw new BadInputException(400, "Expected: create <game name>");
    }

    //TODO: Implement keeping track of the games list, and remove display of GameIDs.
    public String listGames() throws BadFacadeRequestException, BadInputException {
        var games = server.listGames(currAuth.authToken());

        int counter = 0;
        for (GameData game : games) {
            counter++;
            System.out.println(counter + ".");
            System.out.println("    Game Name: " + game.gameName());
            System.out.println("    Game ID: " + game.gameID());
            System.out.println("    Player White: " + game.whiteUsername());
            System.out.println("    Player Black: " + game.blackUsername());
        }
        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Use the Game ID to join or observe a game!";
    }

    //TODO: Implement joining a game based on it's local id in the client, not it's actual gameID.
    public String joinGame(String[] params) throws BadFacadeRequestException, BadInputException {
        if (params.length == 2) {
            if (Objects.equals(params[1], "white")) {
                server.joinGame(currAuth.authToken(), new GameData(Integer.parseInt(params[0]), "WHITE", null, null, null));
                //TODO: Implement the board printing...
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "You joined as the " +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + params[1] +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.RESET_BG_COLOR + " pieces.";
            } else if (Objects.equals(params[1], "black")) {
                server.joinGame(currAuth.authToken(), new GameData(Integer.parseInt(params[0]), null, "BLACK", null, null));
                //TODO: Implement the board printing...
                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "You joined as the " +
                        EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + params[1] +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.RESET_BG_COLOR + " pieces.";
            }
            throw new BadInputException(400, "You can only join as white or black.");
        }
        throw new BadInputException(400, "Expected: play <gameID> <WHITE|BLACK>");
    }

    public String observeGame(String[] params) throws BadFacadeRequestException, BadInputException {
        if (params.length == 1) {
            System.out.println("Not implemented yet, printing default board configuration...");
            printBoards();
        }
        throw new BadInputException(400, "Expected: observe <gameID>");
    }

    private void printBoards() {
        printWhiteView();
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY.repeat(10) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY.repeat(10) + EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        printBlackView();
    }

    private void printWhiteView() {
        System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK +
                EscapeSequences.EMPTY + " a  b  c  d  e  f  g  h " + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
        System.out.println("WHITE REPRESENTATION UNFINISHED");
    }

    private void printBlackView() {
        System.out.println("BLACK REPRESENTATION UNFINISHED.");
    }
}
