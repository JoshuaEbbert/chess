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
        GameService service = new GameService();
        ArrayList<Map<String, Object>> gamesList = service.listGames(authtoken);

        return gson.toJson(Map.of("games", gamesList));
    }
}
