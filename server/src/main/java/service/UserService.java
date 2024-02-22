package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class UserService extends BaseService{
    public AuthData register(UserData user) throws DataAccessException {
        UserData verifiedUser = MemoryUserDAO.getUser(user.username());
        if (verifiedUser != null) {
            throw new DataAccessException("already taken");
        } else {
            MemoryUserDAO.createUser(user.username(), user.password(), user.email());
            return MemoryAuthDAO.createAuth(user.username());
        }
    }
    public AuthData login(UserData loginAttempt) throws DataAccessException {
        UserData verifiedUser = MemoryUserDAO.getUser(loginAttempt.username());
        if (verifiedUser == null || !verifiedUser.password().equals(loginAttempt.password())) {
            throw new DataAccessException("unauthorized");
        } else {
            return MemoryAuthDAO.createAuth(verifiedUser.username());
        }
    }

    public void logout(AuthData logout) throws DataAccessException {
        MemoryAuthDAO.deleteAuth(logout.authToken()); //
    }
}