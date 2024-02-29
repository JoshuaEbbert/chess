package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import model.AuthData;

import java.util.HashSet;

public class SQLAuthDAO implements dataAccess.AuthDAO {
    public static AuthData createAuth(String username) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static void deleteAuth(String authIdentifier) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static AuthData verifyAuth(String authToken) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static void clear() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static HashSet<AuthData> listAuths() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
