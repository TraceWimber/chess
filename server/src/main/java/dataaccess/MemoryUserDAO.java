package dataaccess;

import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemoryUserDAO implements UserDAO {

    private static MemoryUserDAO instance;
    private Set<UserData> db;

    public MemoryUserDAO() {
        db = new HashSet<>();
    }

    public static MemoryUserDAO getInstance() {
        if (instance == null) instance = new MemoryUserDAO();
        return instance;
    }

    @Override
    public boolean createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) == null) return db.add(userData);
        throw new DataAccessException("Error: Attempted Create, but user already exists.");
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
