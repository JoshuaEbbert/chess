package service;

import dataAccess.*;
import model.AuthData;

public class BaseService {
    public void clear() throws DataAccessException {
        MemoryGameDAO.clear();
        MemoryUserDAO.clear();
        MemoryAuthDAO.clear();
    }
}
