package service;

import dataaccess.*;

public class Service {
    protected AuthDAO authDAO;
    protected UserDAO userDAO;
    protected GameDAO gameDAO;

    public Service() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
    }
}
