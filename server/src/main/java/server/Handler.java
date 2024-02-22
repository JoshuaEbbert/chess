package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import service.BaseService;
import service.GameService;
import service.UserService;

public class Handler {

    protected static final Gson gson = new Gson();
    protected static final UserService userService = new UserService();
    protected static final GameService gameService = new GameService();
    protected static final BaseService baseService = new BaseService();
    protected String authorize(spark.Request req) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!MemoryAuthDAO.listAuthTokens().contains(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        return authToken;
    }
}
