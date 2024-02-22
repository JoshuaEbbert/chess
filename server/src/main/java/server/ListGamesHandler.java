package server;

import dataAccess.DataAccessException;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.Map;

public class ListGamesHandler extends Handler {
    private static final ListGamesHandler instance = new ListGamesHandler();
    public static ListGamesHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authtoken = authorize(req);
        ArrayList<Map<String, Object>> gamesList = gameService.listGames(authtoken);

        return gson.toJson(Map.of("games", gamesList));
    }
}
