package serverLogic;

import chess.ChessGame;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import com.google.gson.Gson;

import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.JoinPlayer;

import javax.websocket.*;
import javax.websocket.Endpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {
    private final String socketUrl;
    private final Gson gson = new Gson();
    private final GameHandler gameHandler;
    private final String authorization;
    private Session session;
    public WebSocketFacade(int port, String authorization, GameHandler gameHandler) {
        socketUrl = "ws://localhost:" + port;
        this.authorization = authorization;
        this.gameHandler = gameHandler;
    }
    public void connect() throws Exception {
        URI uri = new URI(socketUrl + "/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(this);
    }

    public void disconnect() throws Exception {
        this.session.close();
        this.session = null;
    }
    public void joinPlayer(int gameID, ChessGame.TeamColor color) {
        JoinPlayer joinPlayer = new JoinPlayer(authorization, gameID, color);
        try {
            sendMessage(gson.toJson(joinPlayer));
        } catch (Exception e) {
            gameHandler.printMessage("Error: Could not join game");
        }
    }

    private void sendMessage(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }
    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        System.out.println("Message received! " + serverMessage.getServerMessageType());
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                gameHandler.updateGame(gson.fromJson(message, LoadGame.class).getGame());
                break;
            case NOTIFICATION:
                gameHandler.printMessage(gson.fromJson(message, Notification.class).getMessage());
                break;
            case ERROR:
                gameHandler.printMessage(gson.fromJson(message, Error.class).getErrorMessage());
                break;
        }
    }
    @OnClose
    public void onClose() {
    }
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    @OnError
    public void onError() {
    }

    public static void main(String[] args) {
        WebSocketFacade webSocket = new WebSocketFacade(8080, "auth", new GameHandler() {
            @Override
            public void updateGame(ChessGame game) {
            }
            @Override
            public void printMessage(String message) {
            }
        });
        try {
            webSocket.connect();
            webSocket.joinPlayer(0, ChessGame.TeamColor.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
            }
    }
}
