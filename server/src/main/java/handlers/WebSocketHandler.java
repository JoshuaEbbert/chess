package handlers;

import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.HashMap;
import java.util.Objects;
@WebSocket
public class WebSocketHandler {
    private static WebSocketSessions sessions = new WebSocketSessions();
    private static Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    @OnWebSocketError
    public void onError(Throwable error) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        // Call appropriate handler
        System.out.println("Command type: " + command.getCommandType());
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                System.out.println("In JOIN_PLAYER block");
                JoinPlayer joinPlayer = gson.fromJson(message, JoinPlayer.class);
                System.out.println("JoinPlayer: " + joinPlayer);
                joinPlayerHandler(session, joinPlayer);
                break;
            case JOIN_OBSERVER:
                // Call join observer handler
                break;
            case MAKE_MOVE:
                // Call make move handler
                break;
            case LEAVE:
                // Call leave handler
                break;
            case RESIGN:
                // Call resign handler
                break;
        }
    }

    private void joinPlayerHandler(Session session, JoinPlayer command) {
        System.out.println("In joinPlayerHandler");
        String username;
        try {
            System.out.println("In try block");
            String auth = command.getAuthString();
            System.out.println("Auth: " + auth);
            username = SQLAuthDAO.verifyAuth(auth).username();
            System.out.println("Username: " + username);
        } catch (DataAccessException e) {
            System.out.println("Unauthorized");
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }
        System.out.println("Joining player " + username + " to game " + command.getGameID());
        try {
            LoadGame loadGame = new LoadGame(Objects.requireNonNull(SQLGameDAO.getGame(command.getGameID())).game());
            sendMessage(session, gson.toJson(loadGame));
            System.out.println("LoadGame: " + gson.toJson(loadGame));
        } catch (DataAccessException e) {
            System.out.println("Game not found");
            sendMessage(session, gson.toJson(new Error("Game not found")));
            return;
        }
        Notification joinNotification = new Notification(username + " joined the game");
        System.out.println("Notified");
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
        System.out.println("Done");
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(int gameID, Session hostClient, String message) {
        System.out.println("In broadcastMessage");
        HashMap<String, Session> gameSessions = sessions.getSessionsForGameID(gameID);
        System.out.println("Number of sessions for game " + gameID + ": " + gameSessions.size());

        for (Session s: gameSessions.values()) {
            if (!s.equals(hostClient)) {
                System.out.println("Sending message to session " + s);
                sendMessage(s, message);
            } else {
                System.out.println("Skipping host client session");
            }
        }
    }
}
