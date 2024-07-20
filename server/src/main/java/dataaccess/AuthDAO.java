package dataaccess;

import model.AuthData;

public interface AuthDAO {
    /**
     * Create a new authToken for given user
     *
     * @param username String name of user
     * @return AuthData object
     */
    AuthData createAuth(String username);

    /**
     * Find AuthData containing given authToken
     *
     * @param authToken authorization to search for
     * @return AuthData object or null if not found
     */
    AuthData getAuth(String authToken);

    /**
     * Delete given authorization from db
     *
     * @param authToken authorization to delete
     */
    void deleteAuth(String authToken);

    /**
     * Clear all AuthData from db
     */
    void clear();
}
