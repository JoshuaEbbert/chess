package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import model.UserData;

public class SQLUserDAO implements dataAccess.UserDAO {
    public static void createUser(String username, String password, String email) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static UserData getUser(String username) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static void clear() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
