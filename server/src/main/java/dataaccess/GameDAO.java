package dataaccess;

import model.GameData;

import java.util.Set;

public interface GameDAO {
    /**
     * Create a new chess game in the db
     *
     * @param gameName String containing name for the game
     * @return new GameData object
     */
    GameData createGame(String gameName);

    /**
     * Find a game in the db
     *
     * @param gameID int ID of the game to search for
     * @return GameData object or null if not found
     */
    GameData getGame(int gameID);

    /**
     * Get list of all games in db
     *
     * @return Set of all games
     */
    Set<GameData> listGames();

    /**
     * Adds given player to a game or makes a move for that player
     *
     * @param gameID int ID of the game to update
     * @param username String name of player
     * @return updated GameData object
     */
    GameData updateGame(int gameID, String username);

    /**
     * Clear all GameData from db
     */
    void clear();
}
