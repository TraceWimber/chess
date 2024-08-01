package dataaccess;

import model.AuthData;

public interface AuthDAO {
    /**
     * Create a new authToken for given user
     *
     * @param authData AuthData object containing new user authentication
     * @return True if successful
     * @throws DataAccessException If the AuthData already exists in the db
     */
    boolean createAuth(AuthData authData) throws DataAccessException;

    /**
     * Find AuthData containing given authToken
     *
     * @param authToken Authorization to search for
     * @return AuthData object or null if not found
     */
    AuthData getAuth(String authToken) throws DataAccessException;

    /**
     * Delete given authorization from db
     *
     * @param authToken Authorization to delete
     * @throws DataAccessException If given authToken doesn't exist in the db
     */
    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * Clear all AuthData from db
     */
    void clear() throws DataAccessException;
}
