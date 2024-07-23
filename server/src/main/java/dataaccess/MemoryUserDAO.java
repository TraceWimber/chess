package dataaccess;

import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemoryUserDAO implements UserDAO {

    private Set<UserData> db;

    public MemoryUserDAO() {
        db = new HashSet<>();
    }

    @Override
    public boolean createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) == null) return db.add(userData);
        throw new DataAccessException("Attempted Create, but user already exists.");
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : db) {
            if (user.username().equals(username)) return user;
        }
        return null;
    }

    @Override
    public void clear() {
        db = new HashSet<>();
    }
}
