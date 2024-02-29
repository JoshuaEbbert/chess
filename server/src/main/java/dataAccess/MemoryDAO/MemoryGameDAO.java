package dataAccess.MemoryDAO;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    static HashSet<GameData> games = new HashSet<GameData>();

    public static int createGame(String gameName) throws DataAccessException {
        int gameID = games.size() + 1000;
        games.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    public static GameData getGame(String gameName) throws DataAccessException {
        for (GameData g : games) {
            if (g.gameName().equals(gameName)) {
                return g;
            }
        }

        return null;
    }
    public static GameData getGame(int gameID) throws DataAccessException {
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                return g;
            }
        }

        return null;
    }

    public static ArrayList<Map<String, Object>> listGames() throws DataAccessException {
        ArrayList<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();

        for (GameData g : games) {
            Map<String, Object> gameDict = new HashMap<>();
            gameDict.put("gameID", g.gameID());
            gameDict.put("whiteUsername", g.whiteUsername());
            gameDict.put("blackUsername", g.blackUsername());
            gameDict.put("gameName", g.gameName());
            gamesList.add(gameDict);
        }

        return gamesList;
    }

    public static void addPlayer(String playerColor, int gameID, String username) throws DataAccessException {
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                if (playerColor.equals("WHITE")) {
                    games.remove(g);
                    games.add(new GameData(gameID, username, g.blackUsername(), g.gameName(), g.game()));
                } else if (playerColor.equals("BLACK")) {
                    games.remove(g);
                    games.add(new GameData(gameID, g.whiteUsername(), username, g.gameName(), g.game()));
                }
                return;
            }
        }
    }

    public static void clear() throws DataAccessException {
        games.clear();
    }
}
