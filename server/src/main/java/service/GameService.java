package service;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameService extends Service {

    //TODO: Verify these funcs to work with the base class.
    public ArrayList<GameData> listGames() {
        return gameDAO.listGames();
    }

    public GameData createGame(String gameName) throws BadRequestException {
        int id = listGames().size();
        GameData newGame = new GameData(id, null, null, gameName, new ChessGame());
        if (gameDAO.createGame(newGame)) return newGame;
        throw new BadRequestException("Error: Creating a game failed.");
    }

    public boolean joinGame() {
        return false;
    }

    public void clear() {
        gameDAO.clear();
    }
}
