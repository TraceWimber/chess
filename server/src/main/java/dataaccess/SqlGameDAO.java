package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SqlGameDAO implements GameDAO {

    @Override
    public boolean createGame(GameData gameData) {
        return false;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
