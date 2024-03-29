package handlers;

import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;
import requests.JoinGameRequest;

import java.util.Map;
import java.util.Objects;

public class JoinGameHandler extends Handler {
    private static final JoinGameHandler instance = new JoinGameHandler();
    public static JoinGameHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authToken = authorize(req);
        JoinGameRequest request = (JoinGameRequest) gson.fromJson(req.body(), JoinGameRequest.class);

        // make sure it's a valid request
        if (request.getGameID() == 0) {
            throw new DataAccessException("bad request");
        } else if (Objects.requireNonNull(SQLGameDAO.getGame(request.getGameID())).game().getTeamTurn() == null) {
            throw new DataAccessException("game already over");
        }
        gameService.joinGame(authToken, request.getPlayerColor(), request.getGameID());
        return gson.toJson(Map.of());
    }
}
