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
        if (MemoryGameDAO.getGame(gameName) != null) {
            throw new DataAccessException("already taken");
        }
        return MemoryGameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, String teamColor, int gameID) throws DataAccessException {
        String username = MemoryAuthDAO.verifyAuth(authToken).username();

        // make sure the game exists
        boolean gameExists = false;
        Map<String, Object> chosenGame = null;
        for (Map<String, Object> game : MemoryGameDAO.listGames()) {
            if ((int) game.get("gameID") == gameID) {
                gameExists = true;
                chosenGame = game;
            }
        }
        if (!gameExists) {
            throw new DataAccessException("bad request");
        }

        // check if the corresponding color is already taken
        if (teamColor != null && teamColor.equals("WHITE")) {
            if (chosenGame.get("whiteUsername") != null) {
                throw new DataAccessException("already taken");
            } else {
                MemoryGameDAO.addPlayer(teamColor, gameID, username);
            }
        } else if (teamColor != null && teamColor.equals("BLACK")) {
            if (chosenGame.get("blackUsername") != null) {
                throw new DataAccessException("already taken");
            } else {
                MemoryGameDAO.addPlayer(teamColor, gameID, username);
            }
        }
    }
}
