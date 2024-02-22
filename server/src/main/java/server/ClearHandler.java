package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.BaseService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ClearHandler extends Handler {
    private static final ClearHandler instance = new ClearHandler();
    private static final Gson gson = new Gson();
    public static ClearHandler getInstance() {
        return instance;
    }
    public String handle(Request req, Response res) throws DataAccessException {
        ClearRequest request = (ClearRequest) gson.fromJson(req.body(), ClearRequest.class);

        BaseService service = new BaseService();
        service.clear();

        return gson.toJson(Map.of());
    }
}
