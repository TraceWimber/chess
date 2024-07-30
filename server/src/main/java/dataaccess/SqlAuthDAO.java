package dataaccess;

import model.AuthData;

public class SqlAuthDAO implements AuthDAO {

    @Override
    public boolean createAuth(AuthData authData) throws DataAccessException {
        return false;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
