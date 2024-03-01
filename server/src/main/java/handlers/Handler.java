package handlers;

import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import model.AuthData;
import service.BaseService;
import service.GameService;
import service.UserService;

import java.util.HashSet;

public class Handler {

    protected static final Gson gson = new Gson();
    protected static final UserService userService = new UserService();
    protected static final GameService gameService = new GameService();
    protected static final BaseService baseService = new BaseService();
    protected String authorize(spark.Request req) throws DataAccessException {
        String authToken = req.headers("Authorization");
        HashSet<AuthData> auths = SQLAuthDAO.listAuths();
        HashSet<String> authTokens = new HashSet<String>();

        for (AuthData a : auths) {
            authTokens.add(a.authToken());
        }
        if (!authTokens.contains(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        return authToken;
    }
}
