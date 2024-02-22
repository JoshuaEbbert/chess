package dataAccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static HashSet<AuthData> auths = new HashSet<AuthData>();

    public static AuthData createAuth(String username) throws DataAccessException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        auths.add(newAuth);
        return newAuth;
    }

    public static void deleteAuth(String authIdentifier) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.username().equals(authIdentifier) || a.authToken().equals(authIdentifier)) {
                auths.remove(a);
                return;
            }
        }

        throw new DataAccessException("unauthorized");
    }

    public static AuthData verifyAuth(String authToken) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }

        throw new DataAccessException("unauthorized");
    }

    public static void clear() throws DataAccessException {
        auths.clear();
    }

    public static HashSet<AuthData> listAuths() {
        return auths;
    }

    public static HashSet<String> listUsernames() {
        HashSet<String> usernames = new HashSet<String>();
        for (AuthData a : auths) {
            usernames.add(a.username());
        }
        return usernames;
    }

    public static HashSet<String> listAuthTokens() {
        HashSet<String> authTokens = new HashSet<String>();
        for (AuthData a : auths) {
            authTokens.add(a.authToken());
        }
        return authTokens;
    }
}
