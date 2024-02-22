package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

public class Handler {

    protected static final Gson gson = new Gson();
    protected String authorize(spark.Request req) throws DataAccessException {
        String authToken = req.headers("Authorization");
        if (!MemoryAuthDAO.listAuthTokens().contains(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        return authToken;
    }
}
