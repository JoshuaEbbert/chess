package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Response;

import java.util.Map;

public class LoginHandler extends Handler {
    private static final LoginHandler instance = new LoginHandler();
    public static LoginHandler getInstance() {
        return instance;
    }
    public String handle(spark.Request req, spark.Response res) throws DataAccessException {
        UserData request = (UserData) gson.fromJson(req.body(), UserData.class);
        if (request.password() == null || request.username() == null) {
            throw new DataAccessException("bad request");
        }

        UserService service = new UserService();
        AuthData auth = service.login(request);

        return new Gson().toJson(Map.of("username", request.username(), "authToken", auth.authToken()));
    }
}
