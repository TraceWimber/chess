package service;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

public class GameServiceTests {
    private static GameService gameService;
    private static GameData game1;
    private static GameData game2;
    private static AuthData user1;

    @BeforeAll
    public static void init() {
        game1 = new GameData(1, null, null, "Game1", new ChessGame());
        game2 = new GameData(2, null, null, "Game2", new ChessGame());
        user1 = new AuthData("1234", "player1");
    }

    @BeforeEach
    public void setup() throws Exception {
        gameService = new GameService();
        gameService.authDAO.clear();
        gameService.gameDAO.clear();
        gameService.authDAO.createAuth(user1);
        gameService.gameDAO.createGame(game1);
        gameService.gameDAO.createGame(game2);
    }

    //------------LIST GAMES positive & negative tests---------------
    @Test
    @DisplayName("List Games Works")
    public void gamesList() throws Exception {
        ArrayList<GameData> gamesList = new ArrayList<>();
        gamesList.add(game1);
        gamesList.add(game2);
        Assertions.assertEquals(gamesList.get(0).gameName(), gameService.listGames(user1).get(0).gameName());
        Assertions.assertEquals(gamesList.get(1).gameName(), gameService.listGames(user1).get(1).gameName());
    }

    @Test
    @DisplayName("Unauthorized List Games")
    public void gamesListAuth() {
        Executable unauthListGames = () -> gameService.listGames(new AuthData("abcd", "player2"));

        Assertions.assertThrows(BadRequestException.class, unauthListGames);
    }

    //------------CREATE GAME positive & negative tests---------------
    @Test
    @DisplayName("Create Game Works")
    public void gameCreation() throws Exception {
        GameData game3 = new GameData(-1, null, null, "Game3", null);
        gameService.createGame(user1, game3);

        Assertions.assertNotNull(gameService.listGames(user1).get(2));
        Assertions.assertEquals("Game3", gameService.listGames(user1).get(2).gameName());
    }

    @Test
    @DisplayName("Unauthorized Create Game")
    public void gameCreateAuth() {
        GameData game3 = new GameData(-1, null, null, "Game3", null);

        Executable unauthCreate = () -> gameService.createGame(new AuthData("abcd", "player2"), game3);

        Assertions.assertThrows(BadRequestException.class, unauthCreate);
    }

    //------------JOIN GAME positive & negative tests---------------
    @Test
    @DisplayName("Join Game Works")
    public void canJoinGame() throws Exception {
        Assertions.assertTrue(gameService.joinGame(user1, new GameData(1, "this one", null, null, null)));
        Assertions.assertTrue(gameService.joinGame(user1, new GameData(1, null, "this one", null, null)));
        Assertions.assertEquals("player1", gameService.gameDAO.getGame(1).whiteUsername());
        Assertions.assertEquals("player1", gameService.gameDAO.getGame(1).blackUsername());
    }

    @Test
    @DisplayName("Game Is Full")
    public void fullGame() throws Exception {
        GameData game = new GameData(1, "this one", null, null, null);
        gameService.joinGame(user1, game);

        Executable gameIsFull = () -> gameService.joinGame(new AuthData("abcd", "player2"), game);

        Assertions.assertThrows(BadRequestException.class, gameIsFull);
    }

    //------------CLEAR test---------------
    @Test
    @DisplayName("Testing Clear")
    public void clearGames() throws Exception {
        gameService.clear();
        ArrayList<GameData> gamesList = new ArrayList<>();
        Assertions.assertEquals(gamesList, gameService.gameDAO.listGames());
    }
}
