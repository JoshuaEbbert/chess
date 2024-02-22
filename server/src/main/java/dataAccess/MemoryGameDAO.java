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

    public static GameData getGame(int gameID) throws DataAccessException {
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                return g;
            }
        }

        throw new DataAccessException("Game not found");
    }

    public static ArrayList<Map<String, Object>> listGames() throws DataAccessException {
        ArrayList<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();
        for (GameData g : games) {
            gamesList.add(Map.of("gameID", g.gameID(), "whiteUsername", g.whiteUsername(), "blackUsername", g.blackUsername(), "gameName", g.gameName()));
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
