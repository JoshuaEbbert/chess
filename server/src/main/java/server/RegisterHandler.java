package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.BaseService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler extends Handler {
    private static final RegisterHandler instance = new RegisterHandler();
    private static final Gson gson = new Gson();
    public static RegisterHandler getInstance() {
        return instance;
    }
    public String handle(Request req, Response res) throws Exception {
        UserData request = (UserData) gson.fromJson(req.body(), UserData.class);
        if (request.email() == null || request.password() == null || request.username() == null) {
            throw new DataAccessException("bad request");
        }

        UserService service = new UserService();
        AuthData auth = service.register(request);

        return new Gson().toJson(Map.of("username", request.username(), "authToken", auth.authToken()));
    }
}
