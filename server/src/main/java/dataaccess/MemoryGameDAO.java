package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    private Map<Integer,GameData> db;

    @Override
    public boolean createGame(GameData gameData) {
        //TODO: decide if I should allow duplicate games with different IDs
        if (db.containsKey(gameData.gameID())) {
            return false;
        }
        db.put(gameData.gameID(), gameData);
        return true;
    }

    @Override
    public GameData getGame(int gameID) {
        return db.get(gameID);
    }

    @Override
    public Map<Integer,GameData> listGames() {
        return db;
    }

    @Override
    public boolean updateGame(GameData gameData) throws DataAccessException {
        if (db.containsKey(gameData.gameID())) {
            db.put(gameData.gameID(), gameData);
            return true;
        }
        throw new DataAccessException("Cannot Update. Game does not exist.");
    }

    @Override
    public void clear() {
        db = new HashMap<>();
    }
}
