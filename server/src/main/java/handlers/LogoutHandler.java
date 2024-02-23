package handlers;

import dataAccess.DataAccessException;
import model.AuthData;

import java.util.Map;

public class LogoutHandler extends Handler {
    private static final LogoutHandler instance = new LogoutHandler();
    public static LogoutHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authToken = authorize(req);

        userService.logout(new AuthData(authToken, ""));

        return gson.toJson(Map.of());
    }
}
