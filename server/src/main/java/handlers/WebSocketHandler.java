package handlers;

import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.JoinObserver;
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
                JoinPlayer joinPlayer = gson.fromJson(message, JoinPlayer.class);
                joinPlayerHandler(session, joinPlayer);
                break;
            case JOIN_OBSERVER:
                JoinObserver joinObserver = gson.fromJson(message, JoinObserver.class);
                joinObserverHandler(session, joinObserver);
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
        String auth;
        try {
            System.out.println("In try block");
            auth = command.getAuthString();
            System.out.println("Auth: " + auth);
            username = SQLAuthDAO.verifyAuth(auth).username();
            System.out.println("Username: " + username);
        } catch (DataAccessException e) {
            System.out.println("Unauthorized");
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }
        System.out.println("Joining player " + username + " to game " + command.getGameID());
        sessions.addSession(command.getGameID(), auth, session);
        try {
            LoadGame loadGame = new LoadGame(Objects.requireNonNull(SQLGameDAO.getGame(command.getGameID())).game());
            sendMessage(session, gson.toJson(loadGame));
            System.out.println("LoadGame: " + gson.toJson(loadGame));
        } catch (DataAccessException e) {
            System.out.println("Game not found");
            sendMessage(session, gson.toJson(new Error("Game not found")));
            return;
        }
        Notification joinNotification = new Notification(username + " joined the game as " + command.getColor().toString().toLowerCase());
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
        System.out.println("Done");
    }

    private void joinObserverHandler(Session session, JoinObserver command) {
        String username;
        String auth;
        try {
            auth = command.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }
        sessions.addSession(command.getGameID(), auth, session);
        try {
            LoadGame loadGame = new LoadGame(Objects.requireNonNull(SQLGameDAO.getGame(command.getGameID())).game());
            sendMessage(session, gson.toJson(loadGame));
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Game not found")));
            return;
        }
        Notification joinNotification = new Notification(username + " joined the game as an observer");
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
        System.out.println("Done adding observer");
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
