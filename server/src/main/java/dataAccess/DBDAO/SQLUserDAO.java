package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements dataAccess.UserDAO {
    public static void createUser(String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getMessage().startsWith("Duplicate entry")) {
                throw new DataAccessException("already taken");
            } else {
                throw new DataAccessException("Error creating user: " + ex.getMessage());
            }
        }
    }
    public static UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT username, password, email FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error getting user: " + ex.getMessage());

        }
    }
    public static void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM users");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing users: " + ex.getMessage());
        }
    }
}
