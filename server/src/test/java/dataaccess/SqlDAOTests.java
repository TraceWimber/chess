package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlDAOTests {

    private static AuthData userAuth1;
    private static GameData game1;
    private static UserData user1;
    private static SqlAuthDAO authDAO;
    private static SqlGameDAO gameDAO;
    private static SqlUserDAO userDAO;
    private static Gson gson;

    @BeforeAll
    public static void init() throws Exception {
        userAuth1 = new AuthData("1234", "player1");
        game1 = new GameData(1, "whitePlayer", "blackPlayer", "game 1", new ChessGame());
        user1 = new UserData("player1", "password", "email@email.com");
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();
        userDAO = new SqlUserDAO();
        gson = new Gson();
        DatabaseManager.createDatabase();
    }

    @BeforeEach
    public void setup() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            String clearAuth = "TRUNCATE TABLE auth";
            String clearGame = "TRUNCATE TABLE game";
            String clearUser = "TRUNCATE TABLE user";

            var pdStmt = conn.prepareStatement(clearAuth);
            pdStmt.executeUpdate();
            pdStmt = conn.prepareStatement(clearGame);
            pdStmt.executeUpdate();
            pdStmt = conn.prepareStatement(clearUser);
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //=========================AuthDAO=======================
    //------------CREATE AUTH positive & negative tests---------------
    @Test
    @DisplayName("Create Auth Works")
    public void createAuth() throws Exception {
        authDAO.createAuth(userAuth1);

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM auth");
            var resultSet = pdStmt.executeQuery();

            resultSet.next();
            String token = resultSet.getString("authToken");
            String usrname = resultSet.getString("username");
            Assertions.assertEquals("1234", token);
            Assertions.assertEquals("player1", usrname);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("User Already Authenticated")
    public void authFail() throws Exception {
        insertUserAuth1();

        Executable createExisting = () -> authDAO.createAuth(userAuth1);

        Assertions.assertThrows(DataAccessException.class, createExisting);
    }

    //------------GET AUTH positive & negative tests------------------
    @Test
    @DisplayName("Get Auth Works")
    public void getAuth() throws Exception {
        insertUserAuth1();

        AuthData auth = authDAO.getAuth("1234");

        Assertions.assertEquals("1234", auth.authToken());
        Assertions.assertEquals("player1", auth.username());
    }

    @Test
    @DisplayName("Can't Find Auth")
    public void missingAuth() throws Exception {
        AuthData auth = authDAO.getAuth("1234");
        Assertions.assertNull(auth);
    }

    //------------DELETE AUTH positive & negative tests---------------
    @Test
    @DisplayName("Delete Auth Works")
    public void deleteAuth() throws Exception {
        insertUserAuth1();

        authDAO.deleteAuth("1234");

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?");
            pdStmt.setString(1, "1234");
            var resultSet = pdStmt.executeQuery();
            Assertions.assertFalse(resultSet.isBeforeFirst());
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("Auth Not Found")
    public void cannotDeleteAuth() {
        Executable delAuth = () -> authDAO.deleteAuth("1234");
        Assertions.assertThrows(DataAccessException.class, delAuth);
    }

    //------------CLEAR test------------------------------------------
    @Test
    @DisplayName("Clear Auth Works")
    public void clearAuth() throws Exception {
        AuthData userAuth2 = new AuthData("5678", "player2");
        AuthData userAuth3 = new AuthData("91011", "player3");
        authDAO.createAuth(userAuth1);
        authDAO.createAuth(userAuth2);
        authDAO.createAuth(userAuth3);
        authDAO.clear();

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM auth");
            var resultSet = pdStmt.executeQuery();
            Assertions.assertFalse(resultSet.isBeforeFirst());
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //=========================GameDAO=======================
    //------------CREATE GAME positive & negative tests---------------
    @Test
    @DisplayName("Create Game Works")
    public void createGame() throws Exception {
        gameDAO.createGame(game1);

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM game");
            var resultSet = pdStmt.executeQuery();

            resultSet.next();
            int id = resultSet.getInt("gameID");
            String white = resultSet.getString("whiteUsername");
            String black = resultSet.getString("blackUsername");
            String gameName = resultSet.getString("gameName");
            String game = resultSet.getString("chessGame");
            Assertions.assertEquals(1, id);
            Assertions.assertEquals("whitePlayer", white);
            Assertions.assertEquals("blackPlayer", black);
            Assertions.assertEquals("game 1", gameName);
            String chessGame = gson.toJson(new ChessGame());
            Assertions.assertEquals(chessGame, game);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("Cannot Have Duplicate IDs")
    public void duplicateCreation() throws Exception {
        GameData game2 = new GameData(1, "player1", "player2", "game2", new ChessGame());
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        // game1 and game2 were both initialized with gameID of 1

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM game WHERE gameID = 1");
            var resultSet = pdStmt.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }
            Assertions.assertEquals(1, rowCount);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //------------GET GAME positive & negative tests------------------
    @Test
    @DisplayName("Get Game Works")
    public void getGame() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)");
            pdStmt.setString(1, game1.whiteUsername());
            pdStmt.setString(2, game1.blackUsername());
            pdStmt.setString(3, game1.gameName());

            var gameJson = gson.toJson(game1.game());
            pdStmt.setString(4, gameJson);

            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        GameData game = gameDAO.getGame(1);

        Assertions.assertEquals(1, game.gameID());
        Assertions.assertEquals("whitePlayer", game.whiteUsername());
        Assertions.assertEquals("blackPlayer", game.blackUsername());
        Assertions.assertEquals("game 1", game.gameName());
    }

    @Test
    @DisplayName("Invalid Game ID")
    public void cannotFindID() throws Exception {
        gameDAO.createGame(game1);

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM game WHERE gameID = 5");
            var resultSet = pdStmt.executeQuery();
            Assertions.assertFalse(resultSet.isBeforeFirst());
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //------------LIST GAMES positive & negative tests----------------
    @Test
    @DisplayName("List Games Works")
    public void listGames() throws Exception {
        GameData game2 = new GameData(2, null, null, "game 2", new ChessGame());
        GameData game3 = new GameData(3, null, null, "game 3", new ChessGame());
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);

        ArrayList<GameData> gamesList = new ArrayList<>();
        gamesList.add(game1);
        gamesList.add(game2);
        gamesList.add(game3);

        Assertions.assertEquals(gamesList.get(0).gameName(), gameDAO.listGames().get(0).gameName());
        Assertions.assertEquals(gamesList.get(1).gameName(), gameDAO.listGames().get(1).gameName());
        Assertions.assertEquals(gamesList.get(2).gameName(), gameDAO.listGames().get(2).gameName());
    }

    @Test
    @DisplayName("Cannot List 0 Games")
    public void emptyList() throws Exception {
        ArrayList<GameData> games = new ArrayList<>();
        Assertions.assertEquals(games, gameDAO.listGames());
    }

    //------------UPDATE GAME positive & negative tests---------------
    @Test
    @DisplayName("Update Game Works")
    public void updateGame() throws Exception {
        gameDAO.createGame(game1);
        GameData updatedGame = new GameData(1, "Billy", "Bob", "game 1", new ChessGame());

        gameDAO.updateGame(updatedGame);
        Assertions.assertEquals(1, gameDAO.getGame(1).gameID());
        Assertions.assertEquals("game 1", gameDAO.getGame(1).gameName());
        Assertions.assertEquals("Billy", gameDAO.getGame(1).whiteUsername());
        Assertions.assertEquals("Bob", gameDAO.getGame(1).blackUsername());
    }

    @Test
    @DisplayName("Game Doesn't Exist")
    public void cannotUpdateGame() {
        Executable noGame = () -> gameDAO.updateGame(game1);
        Assertions.assertThrows(DataAccessException.class, noGame);
    }

    //------------CLEAR test------------------------------------------
    @Test
    @DisplayName("Clear Games Works")
    public void clearGames() throws Exception {
        GameData game2 = new GameData(2, null, null, "game 2", new ChessGame());
        GameData game3 = new GameData(3, null, null, "game 3", new ChessGame());
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);
        gameDAO.clear();

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM game");
            var resultSet = pdStmt.executeQuery();
            Assertions.assertFalse(resultSet.isBeforeFirst());
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //=========================UserDAO=======================
    //------------CREATE USER positive & negative tests---------------
    @Test
    @DisplayName("Create User Works")
    public void createUser() throws Exception {
        userDAO.createUser(user1);

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
            pdStmt.setString(1, "player1");
            var resultSet = pdStmt.executeQuery();

            resultSet.next();
            String usrname = resultSet.getString("username");
            String pass = resultSet.getString("password");
            String email = resultSet.getString("email");

            Assertions.assertEquals("player1", usrname);
            Assertions.assertEquals("password", pass);
            Assertions.assertEquals("email@email.com", email);
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    @DisplayName("User Already Exists")
    public void cantCreateUser() throws Exception {
        userDAO.createUser(user1);

        Executable createFail = () -> userDAO.createUser(user1);

        Assertions.assertThrows(DataAccessException.class, createFail);
    }

    //------------GET USER positive & negative tests------------------
    @Test
    @DisplayName("Get User Works")
    public void getUser() throws Exception {
        userDAO.createUser(user1);
        UserData user = userDAO.getUser("player1");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.username(), user1.username());
        Assertions.assertEquals(user.password(), user1.password());
        Assertions.assertEquals(user.email(), user1.email());
    }

    @Test
    @DisplayName("User Doesn't Exist")
    public void doesNotExist() throws Exception {
        userDAO.createUser(user1);
        Assertions.assertNull(userDAO.getUser("player2"));
    }

    //------------CLEAR test------------------------------------------
    @Test
    @DisplayName("Clear Users Works")
    public void clearUsers() throws Exception {
        UserData user2 = new UserData("player2", "guessable", "email");
        UserData user3 = new UserData("player3", "cheese", "address");

        userDAO.createUser(user1);
        userDAO.createUser(user2);
        userDAO.createUser(user3);
        userDAO.clear();

        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM user");
            var resultSet = pdStmt.executeQuery();
            Assertions.assertFalse(resultSet.isBeforeFirst());
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void insertUserAuth1() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            pdStmt.setString(1, userAuth1.authToken());
            pdStmt.setString(2, userAuth1.username());
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
