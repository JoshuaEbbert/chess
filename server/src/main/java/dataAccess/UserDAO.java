package dataAccess;

import model.UserData;

public interface UserDAO {
    static void createUser(String username, String password, String email) throws DataAccessException {

    }

    static UserData getUser(String username) throws DataAccessException {
        return null;
    }

    static void clear() throws DataAccessException {

    }
}
