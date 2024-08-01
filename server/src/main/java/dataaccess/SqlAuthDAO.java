package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO {

    @Override
    public boolean createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)");
            pdStmt.setString(1, authData.authToken());
            pdStmt.setString(2, authData.username());
            pdStmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken = ?");
            pdStmt.setString(1, authToken);
            var resultSet = pdStmt.executeQuery();

            if (resultSet.next()) {
                String token = resultSet.getString("authToken");
                String usrname = resultSet.getString("username");
                return new AuthData(token, usrname);
            }
            return null;
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?");
            pdStmt.setString(1, authToken);

            int rowsDeleted = pdStmt.executeUpdate();
            if (rowsDeleted <= 0) {throw new DataAccessException("Error: Cannot delete auth. Auth data not found.");}
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("DELETE FROM auth");
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
