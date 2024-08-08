package client;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.BadFacadeRequestException;
import server.Server;
import server.ServerFacade;
import org.junit.jupiter.api.function.Executable;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static UserData user1;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        user1 = new UserData("Trace", "1234", "email");
    }

    @BeforeEach
    public void setup() throws BadFacadeRequestException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //----------------Register positive & negative tests--------------
    @Test
    @DisplayName("Register Works")
    @Order(1)
    public void validRegister() throws BadFacadeRequestException {
        var auth = facade.register(user1);
        Assertions.assertEquals("Trace", auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    @DisplayName("Username Is Taken")
    @Order(2)
    public void invalidRegister() throws BadFacadeRequestException {
        facade.register(user1);
        Executable regUser = () -> facade.register(new UserData("Trace", "5678", "email2"));
        Assertions.assertThrows(BadFacadeRequestException.class, regUser);
    }

    //----------------Login positive & negative tests--------------
    @Test
    @DisplayName("Login Works")
    @Order(3)
    public void validLogin() throws BadFacadeRequestException {
        facade.register(user1);
        var auth = facade.login(user1);
        Assertions.assertEquals("Trace", auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    @DisplayName("Wrong Password")
    @Order(4)
    public void invalidLogin() throws BadFacadeRequestException {
        facade.register(user1);
        Executable badPass = () -> facade.login(new UserData("Trace", "5678", null));
        Assertions.assertThrows(BadFacadeRequestException.class, badPass);
    }

    //----------------Logout positive & negative tests--------------
    @Test
    @DisplayName("Logout Works")
    @Order(5)
    public void validLogout() throws BadFacadeRequestException {
        var auth = facade.register(user1);
        Executable logOut = () -> facade.logout(auth.authToken());
        Assertions.assertDoesNotThrow(logOut);
    }

    @Test
    @DisplayName("Unauthorized Logout")
    @Order(6)
    public void invalidLogout() {
        Executable logOut = () -> facade.logout("fakeAuthToken");
        Assertions.assertThrows(BadFacadeRequestException.class, logOut);
    }

    //----------------CreateGame positive & negative tests--------------
    @Test
    @DisplayName("CreateGame Works")
    @Order(7)
    public void validCreate() throws BadFacadeRequestException {
        var auth = facade.register(user1);
        var game = facade.createGame(auth.authToken(), new GameData(1, null, null, "game1", new ChessGame()));
        Assertions.assertEquals(1, game.gameID());
        Assertions.assertEquals("game1", game.gameName());
    }

    @Test
    @DisplayName("Unauthorized CreateGame")
    @Order(8)
    public void invalidCreate() {
        Executable badCreate = () -> facade.createGame("fakeAuthToken", new GameData(1, null, null, "game1", new ChessGame()));
        Assertions.assertThrows(BadFacadeRequestException.class, badCreate);
    }

    //----------------ListGames positive & negative tests--------------
    @Test
    @DisplayName("ListGames Works")
    @Order(9)
    public void validList() throws BadFacadeRequestException {

    }

    @Test
    @DisplayName("Unauthorized ListGames")
    @Order(10)
    public void invalidList() throws BadFacadeRequestException {

    }

    //----------------JoinGame positive & negative tests--------------

    //----------------Clear test--------------

}
