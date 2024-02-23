package server;

import com.google.gson.Gson;
import handlers.*;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // For example: Spark.get("/hello", (req, res) -> "Hello BYU!");
        Spark.delete("/db", (req, res) -> ClearHandler.getInstance().handle(req, res));
        Spark.delete("/session", (req, res) -> LogoutHandler.getInstance().handle(req, res));
        Spark.post("/user", (req, res) -> RegisterHandler.getInstance().handle(req, res));
        Spark.post("/session", (req, res) -> LoginHandler.getInstance().handle(req, res));
        Spark.get("/game", (req, res) -> ListGamesHandler.getInstance().handle(req, res));
        Spark.post("/game", (req, res) -> CreateGameHandler.getInstance().handle(req, res));
        Spark.put("/game", (req, res) -> JoinGameHandler.getInstance().handle(req, res));
        Spark.exception(Exception.class, (e, req, res) -> res.body(exceptionHandler(e, req, res)));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {
        Server server = new Server();
        String portNumber = "8080";
        server.run(Integer.parseInt(portNumber));
    }

    public static String exceptionHandler(Exception e, spark.Request req, spark.Response res) {
        switch (e.getMessage()) {
            case "bad request":
                res.status(400);
                break;

            case "unauthorized":
                res.status(401);
                break;

            case "already taken":
                res.status(403);
                break;

            default:
                res.status(500);
                break;
        }

        return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
    }

//    private static <T> T getBody(Request request, Class<T> clazz) {
//        var body = new Gson().fromJson(request.body(), clazz);
//        if (body == null) {
//            throw new RuntimeException("missing required body");
//        }
//        return body;
//    }
}
