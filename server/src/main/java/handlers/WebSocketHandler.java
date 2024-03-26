package handlers;

import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.Objects;

public class WebSocketHandler {
    private static WebSocketSessions sessions = new WebSocketSessions();
    private static Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {

    }

    @OnWebSocketClose
    public void onClose(Session session) {

    }

    @OnWebSocketError
    public void onError(Throwable error) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        // Call appropriate handler
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                joinPlayerHandler(session, (JoinPlayer) command);
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
        String username;
        try {
            String auth = command.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }

        try {
            LoadGame loadGame = new LoadGame(Objects.requireNonNull(SQLGameDAO.getGame(command.getGameID())).game());
            sendMessage(session, gson.toJson(loadGame));
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Game not found")));
            return;
        }
        Notification joinNotification = new Notification(username + " joined the game");
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(int gameID, Session hostClient, String message) {
        for (Session s : sessions.getSessionsForGameID(gameID).values()) {
            if (!s.equals(hostClient)) {
                sendMessage(s, message);
            }
        }
    }
}
