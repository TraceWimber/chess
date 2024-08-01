package service;

import dataaccess.*;

public class Service {
    protected AuthDAO authDAO;
    protected UserDAO userDAO;
    protected GameDAO gameDAO;

    protected AuthDAO authMemDAO;
    protected UserDAO userMemDAO;
    protected GameDAO gameMemDAO;

    public Service() {
        //Sql-based code:
        authDAO = new SqlAuthDAO();
        userDAO = new SqlUserDAO();
        gameDAO = new SqlGameDAO();

        authMemDAO = MemoryAuthDAO.getInstance();
        userMemDAO = MemoryUserDAO.getInstance();
        gameMemDAO = MemoryGameDAO.getInstance();
    }
}
