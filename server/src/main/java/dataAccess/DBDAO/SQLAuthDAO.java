package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
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

        return new AuthData(authtoken, username);
    }

    public static void deleteAuth(String authtoken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM auths WHERE authtoken = ?");
            stmt.setString(1, authtoken);
            int rs = stmt.executeUpdate();

            if (rs == 0) {
                throw new DataAccessException("unauthorized");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting auth: " + ex.getMessage());
        }
    }
    public static AuthData verifyAuth(String authtoken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT username, authtoken FROM auths WHERE authtoken = ?");
            stmt.setString(1, authtoken);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new AuthData(rs.getString("authtoken"), rs.getString("username"));
            } else {
                throw new DataAccessException("unauthorized");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting auth: " + ex.getMessage());
        }
    }
    public static void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM auths");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing authentication table ('auths'): " + ex.getMessage());
        }
    }
    public static HashSet<AuthData> listAuths() throws DataAccessException {
        HashSet<AuthData> auths = new HashSet<AuthData>();
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT username, authtoken FROM auths");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                auths.add(new AuthData(rs.getString("authtoken"), rs.getString("username")));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting auth: " + ex.getMessage());
        }

        return auths;
    }
}
