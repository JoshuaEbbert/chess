package handlers;

import dataAccess.DataAccessException;

import java.util.Map;

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
        }
        gameService.joinGame(authToken, request.getPlayerColor(), request.getGameID());
        return gson.toJson(Map.of());
    }
}
