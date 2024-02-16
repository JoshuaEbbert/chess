package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    static HashSet<GameData> games = new HashSet<GameData>();

    public static int createGame(String gameName) throws DataAccessException {
        int gameID = games.size();
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

    public static HashSet<GameData> listGames() throws DataAccessException {
        return games;
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
