package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

public class UserService extends Service {

    /**
     * Registers a new user into the db
     *
     * @param userData The UserData containing the user's username, email, and password
     * @return AuthData For this user's session
     */
    public AuthData register(UserData userData) throws Exception {
        if (userData.username() == null || userData.password() == null) {throw new BadRequestException("Error: Username/Password is required.");}

        UserData user = userDAO.getUser(userData.username());
        if (user != null) {throw new BadRequestException("Error: User already exists under that name.");}

        // Encrypt user password
        userData = new UserData(userData.username(), BCrypt.hashpw(userData.password(), BCrypt.gensalt()), userData.email());

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
    public AuthData login(UserData userData) throws Exception {
        if (userData.username() == null || userData.password() == null) {throw new BadRequestException("Error: Username/Password is required.");}

        UserData user = userDAO.getUser(userData.username());
        if (user == null) {throw new BadRequestException("Error: Incorrect password and/or username.");}

        if (!BCrypt.checkpw(userData.password(), user.password())) {throw new BadRequestException("Error: Incorrect password and/or username.");}

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, userData.username());
        if (authDAO.createAuth(newAuth)) {return newAuth;}

        throw new DataAccessException("Error: Failed to create authentication.");
    }

    /**
     * Logs out a user and deletes their session
     *
     * @param authData The current user's AuthData
     * @return True if successful
     */
    public boolean logout(AuthData authData) throws Exception {
        String token = authenticate(authData.authToken());
        authDAO.deleteAuth(token);
        return true;
    }

    /**
     * Used only for development. Clears the db of all UserData and AuthData.
     * Remove this method before going into production.
     */
    public void clear() throws Exception {
        userDAO.clear();
        authDAO.clear();
    }

    /**
     * Verifies that a given authToken matches one in the db
     *
     * @param token The authentication token to check for
     * @return The token
     */
    private String authenticate(String token) throws Exception {
        AuthData authData = authDAO.getAuth(token);
        if (authData != null) {return authData.authToken();}
        throw new BadRequestException("Error: Unauthorized.");
    }
}
