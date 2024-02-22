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
        AuthData request = gson.fromJson(req.body(), AuthData.class);
        if (!MemoryAuthDAO.listAuthTokens().contains(request.authToken())) {
            throw new DataAccessException("unauthorized");
        }

        UserService service = new UserService();
        service.logout(new AuthData(request.authToken(), ""));

        return new Gson().toJson(Map.of());
    }
}
