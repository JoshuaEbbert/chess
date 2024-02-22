package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Response;

import java.util.Map;

public class LogoutHandler extends Handler {
    private static final LogoutHandler instance = new LogoutHandler();
    public static LogoutHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authToken = authorize(req);

        UserService service = new UserService();
        service.logout(new AuthData(authToken, ""));

        return gson.toJson(Map.of());
    }
}
