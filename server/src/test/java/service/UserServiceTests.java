package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

public class UserServiceTests {
    private static UserService userService;
    private static UserData user1;
    private static UserData user2;
    private static AuthData authUser1;

    @BeforeAll
    public static void init() {
        user1 = new UserData("player1", "hello", "1@gmail.com");
        user2 = new UserData("player2", "xxxxx", "2@yahoo.com");
        authUser1 = new AuthData("abcdef", "player1");
    }

    @BeforeEach
    public void setup() throws Exception {
        userService = new UserService();
        userService.userDAO.createUser(user1);
        userService.userDAO.createUser(user2);
        userService.authDAO.createAuth(authUser1);
    }

    //------------REGISTER positive & negative tests---------------
    @Test
    @DisplayName("Register Works")
    public void registerUser() throws Exception {
        UserData newUser = new UserData("player3", "wowow", "3@byu.edu");
        AuthData authData = userService.register(newUser);
        Assertions.assertEquals(newUser.username(), authData.username());
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    @DisplayName("Register Existing User")
    public void existingUser() {
        UserData newUser = new UserData("player1", "wowow", "3@byu.edu");

        Executable sameUsername = () -> userService.register(newUser);

        Assertions.assertThrows(BadRequestException.class, sameUsername);
    }

    //------------LOGIN positive & negative tests---------------
    @Test
    @DisplayName("Login Works")
    public void loginUser() throws Exception {
        AuthData authData = userService.login(user2);
        Assertions.assertEquals("player2", authData.username());
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    @DisplayName("Wrong Password")
    public void badLogin() {
        UserData wrongPlayer1 = new UserData("player1", "goodbye", null);

        Executable badPassword = () -> userService.login(wrongPlayer1);

        Assertions.assertThrows(BadRequestException.class, badPassword);
    }

    //------------LOGOUT positive & negative tests---------------
    @Test
    @DisplayName("Logout Works")
    public void logoutUser() throws Exception {
        Assertions.assertTrue(userService.logout(authUser1));
        Assertions.assertNull(userService.authDAO.getAuth(authUser1.authToken()));
    }

    @Test
    @DisplayName("Logout NonExistent User")
    public void noSession() {
        AuthData fakeUser = new AuthData("bogus", "player1");

        Executable fakeLogout = () -> userService.logout(fakeUser);

        Assertions.assertThrows(BadRequestException.class, fakeLogout);
    }

    //------------CLEAR test---------------
    @Test
    @DisplayName("Testing Clear")
    public void clearUserAndAuthData() {
        userService.clear();
        Assertions.assertNull(userService.userDAO.getUser("player1"));
        Assertions.assertNull(userService.userDAO.getUser("player2"));
        Assertions.assertNull(userService.authDAO.getAuth(authUser1.authToken()));
    }
}
