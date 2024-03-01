package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.UUID;

public class SQLAuthDAO implements dataAccess.AuthDAO {
    public static AuthData createAuth(String username) throws DataAccessException {
        String authtoken = UUID.randomUUID().toString();
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("INSERT INTO auths (username, authtoken) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, authtoken);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error creating authentication: " + ex.getMessage());
        }

        return new AuthData(username, authtoken);
    }

    public static void deleteAuth(String authIdentifier) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static AuthData verifyAuth(String authToken) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM auths");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing authentication table ('auths'): " + ex.getMessage());
        }
    }
    public static HashSet<AuthData> listAuths() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
