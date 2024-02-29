package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import model.GameData;

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
}
