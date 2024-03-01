package service;

import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLUserDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService extends BaseService{
    public AuthData register(UserData user) throws DataAccessException {
        UserData verifiedUser = SQLUserDAO.getUser(user.username());
        if (verifiedUser != null) {
            throw new DataAccessException("already taken");
        } else {
            SQLUserDAO.createUser(user.username(), user.password(), user.email());
            return SQLAuthDAO.createAuth(user.username());
        }
    }
    public AuthData login(UserData loginAttempt) throws DataAccessException {
        UserData verifiedUser = SQLUserDAO.getUser(loginAttempt.username());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (verifiedUser == null || !encoder.matches(loginAttempt.password(), verifiedUser.password())) {
            throw new DataAccessException("unauthorized");
        } else {
            return SQLAuthDAO.createAuth(verifiedUser.username());
        }
    }

    public void logout(AuthData logout) throws DataAccessException {
        SQLAuthDAO.deleteAuth(logout.authToken()); //
    }
}