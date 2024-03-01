package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SQLGameDAO implements dataAccess.GameDAO {

    public static int createGame(String gameName) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static GameData getGame(String gameName) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static GameData getGame(int gameID) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static ArrayList<Map<String, Object>> listGames() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void addPlayer(String playerColor, int gameID, String username) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("DELETE FROM games");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing games: " + ex.getMessage());
        }
    }
}
