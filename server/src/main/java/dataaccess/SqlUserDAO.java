package dataaccess;

import model.UserData;

public class SqlUserDAO implements UserDAO {

    @Override
    public boolean createUser(UserData userData) throws DataAccessException {
        return false;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clear() {

    }
}
