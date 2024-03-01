package dataAccess.DBDAO;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLGameDAO implements dataAccess.GameDAO {

    private static final Gson gson = new Gson();

    public static int createGame(String gameName) throws DataAccessException {
        int gameID = 0;
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("INSERT INTO games (gameName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, gameName);

            if(stmt.executeUpdate() == 1) {
                try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    generatedKeys.next();
                    gameID = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            if (ex.getMessage().startsWith("Duplicate entry")) {
                throw new DataAccessException("already taken");
            } else {
                throw new DataAccessException("Error creating game: " + ex.getMessage());
            }
        }

        if (gameID == 0) { throw new DataAccessException("Failed to create new game"); }

        return gameID;
    }

    public static GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT gameName, whiteUsername, blackUsername, gameID, game FROM games WHERE gameName = ?");
            stmt.setString(1, gameName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String gameJSON = rs.getString("game");
                ChessGame game = gson.fromJson(gameJSON, ChessGame.class);
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
    }

    public static GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT gameName, whiteUsername, blackUsername, gameID, game FROM games WHERE gameID = ?");
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String gameJSON = rs.getString("game");
                ChessGame game = gson.fromJson(gameJSON, ChessGame.class);
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
    }

    public static ArrayList<Map<String, Object>> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT gameName, whiteUsername, blackUsername, gameID FROM games");
            ResultSet rs = stmt.executeQuery();

            ArrayList<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> gameDict = new HashMap<>();
                gameDict.put("gameID", rs.getInt("gameID"));
                gameDict.put("whiteUsername", rs.getString("whiteUsername"));
                gameDict.put("blackUsername", rs.getString("blackUsername"));
                gameDict.put("gameName", rs.getString("gameName"));
                gamesList.add(gameDict);
            }
            return gamesList;
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
    }

    public static void addPlayer(String playerColor, int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("UPDATE games SET " + playerColor.toLowerCase() + "Username = ? WHERE gameID = ?");
            stmt.setString(1, username);
            stmt.setInt(2, gameID);

            int rs = stmt.executeUpdate();
            if (rs == 0) {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error adding player: " + ex.getMessage());
        }
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
