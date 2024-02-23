package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
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
            String whiteUsername = g.whiteUsername() == null ? "" : g.whiteUsername();
            String blackUsername = g.blackUsername() == null ? "" : g.blackUsername();
            Map<String, Object> gameDict = Map.of("gameID", g.gameID(), "whiteUsername", whiteUsername, "blackUsername", blackUsername, "gameName", g.gameName());
            gamesList.add(gameDict);
        }

        return gamesList;
    }

    public static void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                games.remove(g);
                games.add(g.setGame(updatedGame));
                return;
            }
        }

        throw new DataAccessException("Game not found");
    }

    public static void clear() throws DataAccessException {
        games.clear();
    }
}
