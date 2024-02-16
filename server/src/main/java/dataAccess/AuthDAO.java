package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(String username) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData verifyAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
