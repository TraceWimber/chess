package dataaccess;

import java.util.ArrayList;
import model.GameData;

public interface GameDAO {
    /**
     * Create a new chess game in the db
     *
     * @param gameData GameData object containing the new game
     * @return True if successful
     */
    boolean createGame(GameData gameData) throws DataAccessException;

    /**
     * Find a game in the db
     *
     * @param gameID int ID of the game to search for
     * @return GameData object or null if not found
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * Get list of all games in db
     *
     * @return List of all games
     */
    ArrayList<GameData> listGames() throws DataAccessException;

    /**
     * Adds given player to a game or makes a move for that player
     *
     * @param gameData GameData object containing the updated game
     * @throws DataAccessException If attempting to update a game that doesn't exist
     */
    void updateGame(GameData gameData) throws DataAccessException;

    /**
     * Clear all GameData from db
     */
    void clear() throws DataAccessException;
}
