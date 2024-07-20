package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Create a new user in the db
     *
     * @param userData UserData object containing the new user
     */
    void createUser(UserData userData);

    /**
     * Gets the given player's info from the db
     *
     * @param username String containing name of player to get
     * @return UserData object or null if not found
     */
    UserData getUser(String username);

    /**
     * Clear all UserData from db
     */
    void clear();
}
