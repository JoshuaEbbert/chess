package service;

import dataAccess.*;
import model.AuthData;

public class BaseService {
    public AuthData verifyToken(String token) throws DataAccessException {
        return MemoryAuthDAO.verifyAuth(token);
    }
    public void clear() throws DataAccessException {
        MemoryGameDAO.clear();
        MemoryUserDAO.clear();
        MemoryAuthDAO.clear();
    }
}
