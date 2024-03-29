package handlers;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;

import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
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
        sessions.removeSession(session);
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
                MakeMove makeMove = gson.fromJson(message, MakeMove.class);
                makeMoveHandler(session, makeMove);
                break;
            case LEAVE:
                Leave leaveCommand = gson.fromJson(message, Leave.class);
                leaveHandler(session, leaveCommand);
                System.out.println("Done with leave case");
                break;
            case RESIGN:
                Resign resignCommand = gson.fromJson(message, Resign.class);
                resignHandler(session, resignCommand);
                System.out.println("Done with resign case");
                break;
        }
    }

    private void joinPlayerHandler(Session session, JoinPlayer command) {
        String username;
        String auth;
        GameData gameData;
        try {
            auth = command.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
            // verify gameID is valid
            gameData = SQLGameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendMessage(session, gson.toJson(new Error("Game not found")));
                return;
            }
            // verify that http request has already been sent
            String whitePlayer = gameData.whiteUsername();
            String blackPlayer = gameData.blackUsername();
            if (command.getPlayerColor() == ChessGame.TeamColor.WHITE && !Objects.equals(whitePlayer, username)) {
                sendMessage(session, gson.toJson(new Error("White spot already reserved")));
                return;
            } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK && !Objects.equals(blackPlayer, username)) {
                sendMessage(session, gson.toJson(new Error("Black spot already reserved")));
                return;
            }
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }

        LoadGame loadGame = new LoadGame(gameData.game());
        sendMessage(session, gson.toJson(loadGame));

        sessions.addSession(command.getGameID(), auth, session);
        Notification joinNotification = new Notification(username + " joined the game as " + command.getPlayerColor().toString().toLowerCase());
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
        System.out.println("Joined player " + username + " to game " + command.getGameID());
        System.out.println("Current #Sessions: " + sessions.getSessionsForGameID(command.getGameID()).size());
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

        try {
            GameData gameData = SQLGameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendMessage(session, gson.toJson(new Error("Game not found")));
                return;
            }
            LoadGame loadGame = new LoadGame(gameData.game());
            sendMessage(session, gson.toJson(loadGame));
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Game not found")));
            return;
        }
        sessions.addSession(command.getGameID(), auth, session);
        Notification joinNotification = new Notification(username + " joined the game as an observer");
        broadcastMessage(command.getGameID(), session, gson.toJson(joinNotification));
        System.out.println("# Sessions: " + sessions.getSessionsForGameID(command.getGameID()).size());
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
            if (game.getTeamTurn() == null) {
                sendMessage(session, gson.toJson(new Error("Game already over")));
                return;
            }
            game.setTeamTurn(null);
            SQLGameDAO.updateGame(game, resignCommand.getGameID());
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Error leaving game")));
            return;
        }

        Notification joinNotification = new Notification(username + " resigned from the game. Opponent wins.");
        broadcastMessage(resignCommand.getGameID(), session, gson.toJson(joinNotification));
    }

    private void makeMoveHandler(Session session, MakeMove moveRequest) {
        String username;
        String auth;
        try {
            auth = moveRequest.getAuthString();
            username = SQLAuthDAO.verifyAuth(auth).username();
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Unauthorized")));
            return;
        }

        ChessGame game;
        try {
            game = Objects.requireNonNull(SQLGameDAO.getGame(moveRequest.getGameID())).game();
            try {
                game.makeMove(moveRequest.getMove());
            } catch (InvalidMoveException e) {
                sendMessage(session, gson.toJson(new Error(e.getMessage())));
                return;
            }
            // Update game in database
            SQLGameDAO.updateGame(game, moveRequest.getGameID());
        } catch (DataAccessException e) {
            sendMessage(session, gson.toJson(new Error("Error making move")));
            return;
        }

        broadcastMessage(moveRequest.getGameID(), session, gson.toJson(new Notification(username + " made a move")));
        sendMessage(session, gson.toJson(new LoadGame(game)));
        broadcastMessage(moveRequest.getGameID(), session, gson.toJson(new LoadGame(game)));
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
