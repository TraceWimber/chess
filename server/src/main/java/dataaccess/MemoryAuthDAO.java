package dataaccess;

import model.AuthData;
import java.util.HashSet;
import java.util.Set;

public class MemoryAuthDAO implements AuthDAO {

    private static MemoryAuthDAO instance;
    private Set<AuthData> db;

    public MemoryAuthDAO() {
        db = new HashSet<>();
    }

    public static MemoryAuthDAO getInstance() {
        if (instance == null) {instance = new MemoryAuthDAO();}
        return instance;
    }

    @Override
    public boolean createAuth(AuthData authData) throws DataAccessException {
        if (getAuth(authData.authToken()) == null) {return db.add(authData);}
        throw new DataAccessException("Error: Cannot create auth. User is already authenticated.");
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth : db) {
            if (auth.authToken().equals(authToken)) {return auth;}
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData authData = getAuth(authToken);
        if (authData != null) {
            db.remove(authData);
        }
        else {throw new DataAccessException("Error: Cannot delete auth. Auth data not found.");}
    }

    @Override
    public void clear() {
        db = new HashSet<>();
    }
}
