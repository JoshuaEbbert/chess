package server;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.Map;

public class CreateGameHandler extends Handler {
    private static final CreateGameHandler instance = new CreateGameHandler();
    public static CreateGameHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authToken = authorize(req);
        GameData request = (GameData) gson.fromJson(req.body(), GameData.class);

        if (request.gameName() == null) {
            throw new DataAccessException("bad request");
        }

        return gson.toJson(Map.of("gameID", gameService.createGame(authToken, request.gameName())));
    }
}
