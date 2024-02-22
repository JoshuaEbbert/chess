package dataAccess;

import model.AuthData;

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

    public static void deleteAuth(String username) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.username().equals(username)) {
                auths.remove(a);
                return;
            }
        }

        throw new DataAccessException("Auth not found");
    }

    public static AuthData verifyAuth(String authToken) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }

        throw new DataAccessException("Auth not found");
    }

    public static void clear() throws DataAccessException {
        auths.clear();
    }

    public static HashSet<AuthData> listAuths() {
        return auths;
    }
}
