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
    private static final Gson gson = new Gson();
    public static LogoutHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!MemoryAuthDAO.listAuthTokens().contains(authToken)) {
            throw new DataAccessException("unauthorized");
        }

        UserService service = new UserService();
        service.logout(new AuthData(authToken, ""));

        return new Gson().toJson(Map.of());
    }
}
