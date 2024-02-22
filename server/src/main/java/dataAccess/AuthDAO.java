package dataAccess;

import model.AuthData;

import java.util.HashSet;

public interface AuthDAO {
    static void createAuth(String username) throws DataAccessException {

    }

    static void deleteAuth(String authToken) throws DataAccessException {

    }

    static AuthData verifyAuth(String authToken) throws DataAccessException {
        return null;
    }

    static void clear() throws DataAccessException {

    }

    static HashSet<String> listUsernames() {
        return null;
    }

    static HashSet<String> listAuthTokens() {
        return null;
    }
}
