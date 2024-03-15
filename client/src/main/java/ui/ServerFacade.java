package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.JoinGameRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ServerFacade {
    private final String serverUrl;
    private String authorization;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData login(String username, String password) throws ResponseException {
        String path = "/session";
        AuthData authData = this.makeRequest("POST", path, null, new UserData(username, password, null), AuthData.class);
        this.authorization = authData.authToken();
        return authData;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        String path = "/user";
        AuthData authData = this.makeRequest("POST", path, null, new UserData(username, password, email), AuthData.class);
        this.authorization = authData.authToken();
        return authData;
    }

    public void logout() throws ResponseException {
        String path = "/session";
        this.makeRequest("DELETE", path, authorization, null, null);
        this.authorization = null;
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        this.makeRequest("DELETE", path, authToken, null, null);
        this.authorization = null;
    }

    public int createGame(String gameName) throws ResponseException {
        String path = "/game";
        GameData createdGame = this.makeRequest("POST", path, authorization, new GameData(0, null, null, gameName, null), GameData.class);
        return createdGame.gameID();
    }

    public ArrayList<Map<String, Object>> listGames() throws ResponseException {
        String path = "/game";
        return (ArrayList<Map<String, Object>>) this.makeRequest("GET", path, authorization, null, Map.class).get("games");
    }
    public ArrayList<Map<String, Object>> listGames(String authToken) throws ResponseException {
        String path = "/game";
        return (ArrayList<Map<String, Object>>) this.makeRequest("GET", path, authToken, null, Map.class).get("games");
    }

    public void joinGame(String color, int gameID) throws ResponseException {
        String path = "/game";
        this.makeRequest("PUT", path, authorization, new JoinGameRequest(gameID, color), null);
    }

    private <T> T makeRequest(String method, String path, String authorization, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setReadTimeout(5000); // 5 seconds
            http.setRequestMethod(method);
            if (authorization != null) {
                http.addRequestProperty("Authorization", authorization);
            }
            if (Objects.equals(method, "POST") || Objects.equals(method, "PUT")) {
                http.setDoOutput(true);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (status != 200) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
