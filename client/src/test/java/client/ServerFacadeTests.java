package client;

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


    //----------------Logout positive & negative tests--------------

    //----------------ListGames positive & negative tests--------------

    //----------------CreateGame positive & negative tests--------------

    //----------------JoinGame positive & negative tests--------------

    //----------------Clear test--------------

}
