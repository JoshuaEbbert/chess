package service;

import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;

import java.util.ArrayList;
import java.util.Map;

public class GameService extends BaseService{
    public ArrayList<Map<String, Object>> listGames(String authToken) throws DataAccessException {
        SQLAuthDAO.verifyAuth(authToken);

        return SQLGameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        SQLAuthDAO.verifyAuth(authToken);
        if (SQLGameDAO.getGame(gameName) != null) {
            throw new DataAccessException("already taken");
        }
        return SQLGameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, String teamColor, int gameID) throws DataAccessException {
        String username = SQLAuthDAO.verifyAuth(authToken).username();

        // make sure the game exists
        boolean gameExists = false;
        Map<String, Object> chosenGame = null;
        for (Map<String, Object> game : SQLGameDAO.listGames()) {
            if ((int) game.get("gameID") == gameID || game.get("gameID").equals(gameID)) {
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
                SQLGameDAO.addPlayer(teamColor, gameID, username);
            }
        } else if (teamColor != null && teamColor.equals("BLACK")) {
            if (chosenGame.get("blackUsername") != null) {
                throw new DataAccessException("already taken");
            } else {
                SQLGameDAO.addPlayer(teamColor, gameID, username);
            }
        }
    }
}
