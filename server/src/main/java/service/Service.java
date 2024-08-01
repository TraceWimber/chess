package service;

import dataaccess.*;

public class Service {
    protected AuthDAO authDAO;
    protected UserDAO userDAO;
    protected GameDAO gameDAO;

    public Service() {
        // If you want to use the memory-based version of this, uncomment these lines below
        // authDAO = MemoryAuthDAO.getInstance();
        // userDAO = MemoryUserDAO.getInstance();
        // gameDAO = MemoryGameDAO.getInstance();

        //Sql-based code:
        authDAO = new SqlAuthDAO();
        userDAO = new SqlUserDAO();
        gameDAO = new SqlGameDAO();
    }
}
