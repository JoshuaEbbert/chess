package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class GameService extends BaseService{
    public ArrayList<Map<String, Object>> listGames(String authToken) throws DataAccessException {
        MemoryAuthDAO.verifyAuth(authToken);

        return MemoryGameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        MemoryAuthDAO.verifyAuth(authToken);
        return MemoryGameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, ChessGame.TeamColor clientColor, int gameID) {
        throw new UnsupportedOperationException();
    }
}
