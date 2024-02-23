package handlers;

import dataAccess.DataAccessException;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ClearHandler extends Handler {
    private static final ClearHandler instance = new ClearHandler();
    public static ClearHandler getInstance() {
        return instance;
    }
    public String handle(Request req, Response res) throws DataAccessException {
        ClearRequest request = (ClearRequest) gson.fromJson(req.body(), ClearRequest.class);

        baseService.clear();

        return gson.toJson(Map.of());
    }
}
