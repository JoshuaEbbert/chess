package dataAccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static HashSet<AuthData> auths = new HashSet<AuthData>();

    public static void createAuth(String username) throws DataAccessException {
        auths.add(new AuthData(UUID.randomUUID().toString(), username));
    }

    public static void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.authToken().equals(authToken)) {
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
}
