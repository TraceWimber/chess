package service;

import dataaccess.*;

public class Service {
    protected AuthDAO authDAO;
    protected UserDAO userDAO;
    protected GameDAO gameDAO;

    public Service() {
        authDAO = MemoryAuthDAO.getInstance();
        userDAO = MemoryUserDAO.getInstance();
        gameDAO = MemoryGameDAO.getInstance();
    }
}
