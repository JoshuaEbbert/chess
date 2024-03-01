package dataAccess.MemoryDAO;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

import java.util.Collection;
import java.util.HashSet;
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
}
