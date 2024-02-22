package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.BaseService;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler {
    private static final RegisterHandler instance = new RegisterHandler();
    private static final Gson gson = new Gson();
    public static RegisterHandler getInstance() {
        return instance;
    }
    public String handle(Request req, Response res) throws DataAccessException {
        UserData request = (UserData) gson.fromJson(req.body(), UserData.class);

        UserService service = new UserService();
        AuthData auth = service.register(request);

//        RegisterResponse response = new RegisterResponse("Success");
//        return gson.toJson(response);
    }
}
