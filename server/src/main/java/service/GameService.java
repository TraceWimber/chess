package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService extends Service {

    /**
     * Gets a list of all games
     *
     * @param authData The authentication data of the user
     * @return ArrayList of all games
     */
    public ArrayList<GameData> listGames(AuthData authData) throws BadRequestException {
        String user = authenticate(authData.authToken());
        return gameDAO.listGames();
    }

    /**
     * Creates a new game
     *
     * @param authData The authentication data of the user
     * @param gameData Contains the name for the new game
     * @return GameData for the new game
     */
    public GameData createGame(AuthData authData, GameData gameData) throws BadRequestException {
        String user = authenticate(authData.authToken());
        int id = gameDAO.listGames().size();

        // Create new GameData and check that addition to db is successful
        GameData newGame = new GameData(id, null, null, gameData.gameName(), new ChessGame());
        if (gameDAO.createGame(newGame)) return newGame;

        throw new BadRequestException("Error: Creating a game failed.");
    }

    /**
     * Creates a new game
     *
     * @param authData The authentication data of the user
     * @param gameData Contains which team the player wants to join
     * @return True if successful
     */
    public boolean joinGame(AuthData authData, GameData gameData) throws BadRequestException, DataAccessException {
        String user = authenticate(authData.authToken());
        GameData game = gameDAO.getGame(gameData.gameID());

        if (game == null) throw new BadRequestException("Error: Unable to join. Game does not exist.");

        if (gameData.whiteUsername() != null) {
            // If slot is empty, perform the join
            if (game.whiteUsername() == null) {
                GameData updatedGame = game.addWhite(user);
                gameDAO.updateGame(updatedGame);
                return true;
            }
            //otherwise error
            throw new BadRequestException("Error: Unable to join. That team is occupied.");
        } else if (gameData.blackUsername() != null) {
            if (game.blackUsername() == null) {
                GameData updatedGame = game.addBlack(user);
                gameDAO.updateGame(updatedGame);
                return true;
            }
            throw new BadRequestException("Error: Unable to join. That team is occupied.");
        }
        return false;
    }

    /**
     * Used only for development. Clears the db of all GameData.
     * Remove this method before going into production.
     */
    public void clear() {
        gameDAO.clear();
    }

    /**
     * Verifies that a given authToken matches one in the db
     *
     * @param token The authentication token to check for
     * @return The username associated with the given token
     */
    private String authenticate(String token) throws BadRequestException {
        AuthData authData = authDAO.getAuth(token);
        if (authData != null) return authData.username();
        throw new BadRequestException("Error: Unauthorized.");
    }
}
