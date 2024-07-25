package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService extends Service {

    /**
     * Registers a new user into the db
     *
     * @param userData The UserData containing the user's username, email, and password
     * @return AuthData For this user's session
     */
    public AuthData register(UserData userData) throws DataAccessException, BadRequestException {
        UserData user = userDAO.getUser(userData.username());
        if (user != null) throw new BadRequestException("Error: User already exists under that name.");
        if (userDAO.createUser(userData)) {
            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, userData.username());
            authDAO.createAuth(newAuth);
            return newAuth;
        }
        throw new DataAccessException("Error: Failed to create new user.");
    }

    /**
     * Logs in an existing user
     *
     * @param userData The UserData containing the user's username and password
     * @return AuthData For this user's session
     */
    public AuthData login(UserData userData) throws BadRequestException, DataAccessException {
        UserData user = userDAO.getUser(userData.username());
        if (user == null) throw new BadRequestException("Error: Incorrect password or username.");
        if (!Objects.equals(user.password(), userData.password())) throw new BadRequestException("Error: Incorrect password or username.");
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, userData.username());
        if (authDAO.createAuth(newAuth)) return newAuth;
        throw new DataAccessException("Error: Failed to create authentication.");
    }

    /**
     * Logs out a user and deletes their session
     *
     * @param authData The current user's AuthData
     * @return True if successful
     */
    public boolean logout(AuthData authData) throws BadRequestException, DataAccessException {
        String token = authenticate(authData.authToken());
        authDAO.deleteAuth(token);
        return true;
    }

    /**
     * Used only for development. Clears the db of all UserData and AuthData.
     * Remove this method before going into production.
     */
    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    /**
     * Verifies that a given authToken matches one in the db
     *
     * @param token The authentication token to check for
     * @return The token
     */
    private String authenticate(String token) throws BadRequestException {
        AuthData authData = authDAO.getAuth(token);
        if (authData != null) return authData.authToken();
        throw new BadRequestException("Error: Unauthorized.");
    }
}
