package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    @Override
    public boolean createUser(UserData userData) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            UserData usrD = getUser(userData.username());
            if (usrD == null) {
                var pdStmt = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)");
                pdStmt.setString(1, userData.username());
                pdStmt.setString(2, userData.password());
                pdStmt.setString(3, userData.email());
                pdStmt.executeUpdate();
                return true;
            }
            throw new DataAccessException("Error: Attempted Create, but user already exists.");
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("SELECT username, password, email FROM user WHERE username = ?");
            pdStmt.setString(1, username);
            var resultSet = pdStmt.executeQuery();

            if (resultSet.next()) {
                String usrname = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String email = resultSet.getString("email");
                return new UserData(usrname, pass, email);
            }
            return null;
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var pdStmt = conn.prepareStatement("TRUNCATE TABLE user");
            pdStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
