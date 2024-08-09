package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.BadFacadeRequestException;
import server.BadInputException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ChessClient {

    private final ServerFacade server;
    private boolean isSignedIn = false;
    private AuthData currAuth;
    private UserData currUser;
    private ArrayList<GameData> gamesList;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        currAuth = null;
        gamesList = new ArrayList<>();
    }

    /**
     * Evaluates the user input to determine which command was given.
     * If an invalid command was given, displays the help menu
     *
     * @param input the string of input given by the user
     * @return a string response indicating status of the client
     */
    public String eval(String input) {
        try {
            // Menu for unauthenticated users
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

            // Menu for authenticated users
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
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

    /**
     * Logs the user in
     *
     * @param params An array containing all the parameters included in the user's command
     * @return success or failure message
     */
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

    /**
     * Registers the user
     *
     * @param params An array containing all the parameters included in the user's command
     * @return success or failure message
     */
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

    /**
     * The help menu. Displays different commands based on
     * if the user has logged in or not
     *
     * @return string containing the help text
     */
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

    /**
     * Logs the user out
     *
     * @return success or failure message
     */
    public String logout() throws BadFacadeRequestException, BadInputException {
        server.logout(currAuth.authToken());
        isSignedIn = false;
        currAuth = null;

        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged out!";
    }

    /**
     * Creates a new game
     *
     * @param params An array containing all the parameters included in the user's command
     * @return success or failure message
     */
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

    /**
     * Prints all games
     *
     * @return success or failure message
     */
    public String listGames() throws BadFacadeRequestException, BadInputException {
        gamesList = server.listGames(currAuth.authToken());

        if (gamesList.isEmpty()) {
            return EscapeSequences.SET_TEXT_COLOR_YELLOW + "There are no games. Use 'create' to start one!";
        }
        int counter = 0;
        for (GameData game : gamesList) {
            counter++;
            System.out.println(counter + ".");
            System.out.println("    Game Name: " + game.gameName());
            System.out.println("    Player White: " + game.whiteUsername());
            System.out.println("    Player Black: " + game.blackUsername());
        }
        return EscapeSequences.SET_TEXT_COLOR_YELLOW + "Use the game number to join or observe a game!";
    }

    /**
     * Joins the user to the given game as the given team
     *
     * @param params An array containing all the parameters included in the user's command
     * @return success or failure message
     */
    public String joinGame(String[] params) throws BadFacadeRequestException, BadInputException {
        gamesList = server.listGames(currAuth.authToken());

        if (params.length == 2) {
            int gameID = gamesList.get(Integer.parseInt(params[0]) - 1).gameID();

            // Joins player to the white team
            if (Objects.equals(params[1], "white")) {
                server.joinGame(currAuth.authToken(), new GameData(gameID, "WHITE", null, null, null));

                printWhiteView(gamesList.get(Integer.parseInt(params[0]) - 1).game().getBoard());

                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "You joined as the " +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + params[1] +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.RESET_BG_COLOR + " pieces.";
            }
            // Joins player to the black team
            else if (Objects.equals(params[1], "black")) {
                server.joinGame(currAuth.authToken(), new GameData(gameID, null, "BLACK", null, null));

                printBlackView(gamesList.get(Integer.parseInt(params[0]) - 1).game().getBoard());

                return EscapeSequences.SET_TEXT_COLOR_YELLOW + "You joined as the " +
                        EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + params[1] +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.RESET_BG_COLOR + " pieces.";
            }
            throw new BadInputException(400, "You can only join as white or black.");
        }
        throw new BadInputException(400, "Expected: play <gameID> <WHITE|BLACK>");
    }

    /**
     * Puts the user into observation mode
     *
     * @param params An array containing all the parameters included in the user's command
     * @return success or failure message
     */
    public String observeGame(String[] params) throws BadFacadeRequestException, BadInputException {
        gamesList = server.listGames(currAuth.authToken());

        if (params.length == 1) {
            printBoards(gamesList.get(Integer.parseInt(params[0]) - 1).game());
            return "Observing Game " + params[0] + "\n";
        }
        throw new BadInputException(400, "Expected: observe <gameID>");
    }

    // prints a representation of the given game from both WHITE and BLACK players' perspectives
    private void printBoards(ChessGame game) {
        printWhiteView(game.getBoard());
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY.repeat(10) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY.repeat(10) + EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        printBlackView(game.getBoard());
    }

    // prints a representation of the given board from the WHITE player's perspective
    private void printWhiteView(ChessBoard board) {
        StringBuilder boardDisplay = new StringBuilder();

        boardDisplay.append(letterCoords());

        for (int i = 0; i < 8; i++) {
            boardDisplay.append(printCols(board, i));
        }

        boardDisplay.append(letterCoords());

        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(boardDisplay);
    }

    // prints a representation of the given board from the BLACK player's perspective
    private void printBlackView(ChessBoard board) {
        StringBuilder boardDisplay = new StringBuilder();

        boardDisplay.append(letterCoords());

        for (int i = 7; i >= 0; i--) {
            boardDisplay.append(printCols(board, i));
        }

        boardDisplay.append(letterCoords());

        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(boardDisplay);
    }

    private static String printCols(ChessBoard board, int i) {
        StringBuilder innerBuilder = new StringBuilder();

        innerBuilder.append(" ").append(8 - i).append(" ");

        for (int j = 0; j < 8; j++) {
            ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));

            if ((i + j) % 2 == 0) {
                innerBuilder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            }
            else {
                innerBuilder.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            }

            if (piece == null) {
                innerBuilder.append(EscapeSequences.EMPTY);
            }
            else {
                innerBuilder.append(EscapeSequences.SET_TEXT_COLOR_WHITE).append(getPieceUnicode(piece));
            }

            innerBuilder.append(EscapeSequences.RESET_BG_COLOR);
        }

        innerBuilder.append(EscapeSequences.SET_TEXT_COLOR_GREEN).append(" ").append(8 - i).append(" \n");

        return innerBuilder.toString();
    }

    // returns the ANSI Escape Sequence for a given ChessPiece
    private static String getPieceUnicode(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case ChessPiece.PieceType.QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ChessPiece.PieceType.BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case ChessPiece.PieceType.KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ChessPiece.PieceType.ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case ChessPiece.PieceType.PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    // returns a string representation of the letter coordinates used for the top and bottom of a chess board
    private static String letterCoords() {
        StringBuilder letterBuilder = new StringBuilder();

        letterBuilder.append(EscapeSequences.SET_TEXT_COLOR_GREEN).append("   ");
        letterBuilder.append(" Ａ ");
        letterBuilder.append(" Ｂ ");
        letterBuilder.append(" Ｃ ");
        letterBuilder.append(" Ｄ ");
        letterBuilder.append(" Ｅ ");
        letterBuilder.append(" Ｆ ");
        letterBuilder.append(" Ｇ ");
        letterBuilder.append(" Ｈ ");
        letterBuilder.append("\n");

        return letterBuilder.toString();
    }
}
