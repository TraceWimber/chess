package dataaccess;

import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> db;

    public MemoryGameDAO() {
        db = new ArrayList<>();
    }

    @Override
    public boolean createGame(GameData gameData) {
        if (gameData.gameID() == db.size()) {
            db.add(gameData);
            return true;
        }
        return false;
    }

    @Override
    public GameData getGame(int gameID) {
        return db.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() {
        return db;
    }

    @Override
    public boolean updateGame(GameData gameData) throws DataAccessException {
        if (gameData.gameID() < db.size()) {
            db.set(gameData.gameID(), gameData);
            return true;
        }
        throw new DataAccessException("Cannot Update. Game does not exist.");
    }

    @Override
    public void clear() {
        db = new ArrayList<>();
    }
}
