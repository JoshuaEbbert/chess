package handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

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
                System.out.println("In leave case");
                Leave leaveCommand = gson.fromJson(message, Leave.class);
                leaveHandler(session, leaveCommand);
                System.out.println("Done with leave case");
                break;
            case RESIGN:
                System.out.println("In resign case");
                Resign resignCommand = gson.fromJson(message, Resign.class);
                resignHandler(session, resignCommand);
                System.out.println("Done with resign case");
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

    private void leaveHandler(Session session, Leave leaveCommand) {
        String username;
        String auth;
        try {
            auth = leaveCommand.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }

        if (leaveCommand.getColor() != null) {
            try {
                SQLGameDAO.removePlayer(leaveCommand.getColor().toString().toLowerCase(), leaveCommand.getGameID());
            } catch (DataAccessException e) {
                sendMessage(session, gson.toJson(new Error("Error leaving game")));
                return;
            }
        }

        Notification joinNotification = new Notification(username + " left the game");
        broadcastMessage(leaveCommand.getGameID(), session, gson.toJson(joinNotification));
        sessions.removeSession(session);
        System.out.println("Done leaving game");
    }

    private void resignHandler(Session session, Resign resignCommand) {
        String username;
        String auth;
        try {
            auth = resignCommand.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }

        try {
            ChessGame game = Objects.requireNonNull(SQLGameDAO.getGame(resignCommand.getGameID())).game();
            game.setTeamTurn(null);
            SQLGameDAO.updateGame(game, resignCommand.getGameID());
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Error leaving game")));
            return;
        }

        Notification joinNotification = new Notification(username + " resigned from the game. Opponent wins.");
        broadcastMessage(resignCommand.getGameID(), session, gson.toJson(joinNotification));
        sendMessage(session, gson.toJson(new Notification("You have resigned. Opponent wins.")));
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
