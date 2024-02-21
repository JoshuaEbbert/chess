package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class UserService extends BaseService{
    public AuthData register(UserData user) {
        // try to access user
        // in catch, register the user

        return new AuthData("token", user.username());
    }
    public AuthData login(UserData loginAttempt) throws DataAccessException {
        UserData verifiedUser = MemoryUserDAO.getUser(loginAttempt.username());
        return MemoryAuthDAO.createAuth(verifiedUser.username());
    }
    public void logout(UserData loginAttempt) throws DataAccessException {
        UserData verifiedUser = MemoryUserDAO.getUser(loginAttempt.username());
        MemoryAuthDAO.deleteAuth(verifiedUser.username());
    }
}