package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    static int createGame(String gameName) throws DataAccessException // returns gameID
    {
        return 0;
    }

    static GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    static HashSet<GameData> listGames() throws DataAccessException {
        return null;
    }

    static void clear() throws DataAccessException {

    }
}
