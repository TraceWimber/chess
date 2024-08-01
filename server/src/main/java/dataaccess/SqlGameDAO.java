package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.sql.SQLException;
import java.util.ArrayList;

public class SqlGameDAO implements GameDAO {

    private final Gson GSON = new Gson();

    @Override
    public boolean createGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)");
            pdStmt.setString(1, gameData.whiteUsername());
            pdStmt.setString(2, gameData.blackUsername());
            pdStmt.setString(3, gameData.gameName());

            var gameJson = GSON.toJson(gameData.game());
            pdStmt.setString(4, gameJson);

            pdStmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?");
            pdStmt.setInt(1, gameID);
            var resultSet = pdStmt.executeQuery();

            if (resultSet.next()) {
                String whitePlayer = resultSet.getString("whiteUsername");
                String blackPlayer = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameJson = resultSet.getString("chessGame");
                ChessGame game = GSON.fromJson(gameJson, ChessGame.class);

                return new GameData(gameID, whitePlayer, blackPlayer, gameName, game);
            }
            throw new DataAccessException("Error: Invalid game ID.");
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT * FROM game");
            var resultSet = pdStmt.executeQuery();

            ArrayList<GameData> gameList = new ArrayList<>();
            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whitePlayer = resultSet.getString("whiteUsername");
                String blackPlayer = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameJson = resultSet.getString("chessGame");
                ChessGame game = GSON.fromJson(gameJson, ChessGame.class);
                gameList.add(new GameData(gameID, whitePlayer, blackPlayer, gameName, game));
            }
            return gameList;
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?");
            pdStmt.setString(1, gameData.whiteUsername());
            pdStmt.setString(2, gameData.blackUsername());
            pdStmt.setString(3, gameData.gameName());

            var gameJson = GSON.toJson(gameData.game());
            pdStmt.setString(4, gameJson);

            pdStmt.setInt(5, gameData.gameID());

            if (pdStmt.executeUpdate() <= 0) {throw new DataAccessException("Error: Cannot Update. Game does not exist.");}
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("TRUNCATE TABLE game");
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
