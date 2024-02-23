package handlers;

import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

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

        AuthData auth = userService.login(request);

        return gson.toJson(Map.of("username", request.username(), "authToken", auth.authToken()));
    }
}
