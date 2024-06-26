package handlers;

import chess.ChessGame;
import chess.ChessPiece;
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
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        // Call appropriate handler
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
                break;
            case RESIGN:
                Resign resignCommand = gson.fromJson(message, Resign.class);
                resignHandler(session, resignCommand);
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

        if (getTeamColorByAuth(auth, resignCommand.getGameID()) == null) {
            sendMessage(session, gson.toJson(new Error("Can't resign from a game you're not playing")));
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

        Notification resignNotification = new Notification(username + " resigned from the game. Opponent wins.");
        broadcastMessage(resignCommand.getGameID(), session, gson.toJson(resignNotification));
        resignNotification = new Notification("You resigned from the game. Opponent wins.");
        sendMessage(session, gson.toJson(resignNotification));
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
            ChessGame.TeamColor teamColor = getTeamColorByAuth(auth, moveRequest.getGameID());
            // Player can only move their pieces
            ChessPiece movingPiece = game.getBoard().getPiece(moveRequest.getMove().getStartPosition());
            if ((movingPiece.getTeamColor() != teamColor)) {
                sendMessage(session, gson.toJson(new Error("Can't move that piece")));
                return;
            }

            try {
                game.makeMove(moveRequest.getMove());
            } catch (InvalidMoveException e) {
                sendMessage(session, gson.toJson(new Error(e.getMessage())));
                return;
            }
            // check/checkmate
            ChessGame.TeamColor opponentColor = teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            if (game.isInCheck(opponentColor)) {
                if (game.isInCheckmate(opponentColor)) {
                    game.setTeamTurn(null);
                    Notification checkmateNotification = new Notification("Checkmate! " + teamColor + " wins!");
                    broadcastMessage(moveRequest.getGameID(), session, gson.toJson(checkmateNotification));
                    checkmateNotification = new Notification("Checkmate! " + teamColor + " wins!");
                    sendMessage(session, gson.toJson(checkmateNotification));
                } else {
                    Notification checkNotification = new Notification("Check!");
                    broadcastMessage(moveRequest.getGameID(), session, gson.toJson(checkNotification));
                    checkNotification = new Notification("Check!");
                    sendMessage(session, gson.toJson(checkNotification));
                }
            }

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
        HashMap<String, Session> gameSessions = sessions.getSessionsForGameID(gameID);

        for (Session s: gameSessions.values()) {
            if (!s.equals(hostClient)) {
                sendMessage(s, message);
            }
        }
    }

    private ChessGame.TeamColor getTeamColorByAuth(String auth, int gameID) {
        try {
            String username = SQLAuthDAO.verifyAuth(auth).username();
            GameData gameData = SQLGameDAO.getGame(gameID);
            if (gameData == null) {
                return null;
            }
            if (gameData.whiteUsername().equals(username)) {
                return ChessGame.TeamColor.WHITE;
            } else if (gameData.blackUsername().equals(username)) {
                return ChessGame.TeamColor.BLACK;
            }
        } catch (DataAccessException e) {
            return null;
        }
        return null;
    }
}
