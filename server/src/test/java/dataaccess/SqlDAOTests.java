package dataaccess;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.sql.SQLException;

public class SqlDAOTests {

    private static AuthData userAuth1;
    private static SqlAuthDAO authDAO;
    private static SqlGameDAO gameDAO;
    private static SqlUserDAO userDAO;

    @BeforeAll
    public static void init() throws Exception {
        userAuth1 = new AuthData("1234", "player1");
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();
        userDAO = new SqlUserDAO();
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
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            pdStmt.setString(1, userAuth1.authToken());
            pdStmt.setString(2, userAuth1.username());
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        Executable createExisting = () -> authDAO.createAuth(userAuth1);

        Assertions.assertThrows(DataAccessException.class, createExisting);
    }

    //------------GET AUTH positive & negative tests------------------
    @Test
    @DisplayName("Get Auth Works")
    public void getAuth() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            pdStmt.setString(1, userAuth1.authToken());
            pdStmt.setString(2, userAuth1.username());
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

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
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            pdStmt.setString(1, userAuth1.authToken());
            pdStmt.setString(2, userAuth1.username());
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

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
    @DisplayName("Clear Works")
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


    //=========================UserDAO=======================


}
