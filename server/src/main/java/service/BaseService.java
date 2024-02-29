package service;

import dataAccess.*;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryGameDAO;
import dataAccess.MemoryDAO.MemoryUserDAO;

public class BaseService {
    public void clear() throws DataAccessException {
        MemoryGameDAO.clear();
        MemoryUserDAO.clear();
        MemoryAuthDAO.clear();
    }
}
