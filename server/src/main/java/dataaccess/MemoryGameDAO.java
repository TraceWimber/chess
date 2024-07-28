package dataaccess;

import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private static MemoryGameDAO instance;
    private ArrayList<GameData> db;

    public MemoryGameDAO() {
        db = new ArrayList<>();
    }

    public static MemoryGameDAO getInstance() {
        if (instance == null) instance = new MemoryGameDAO();
        return instance;
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
    public GameData getGame(int gameID) throws DataAccessException {
        if (gameID >= db.size() || gameID < 0) throw new DataAccessException("Error: Invalid game ID.");
        return db.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() {
        return db;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (gameData.gameID() < db.size()) {
            db.set(gameData.gameID(), gameData);
        }
        else {
            throw new DataAccessException("Error: Cannot Update. Game does not exist.");
        }
    }

    @Override
    public void clear() {
        db = new ArrayList<>();
    }
}
