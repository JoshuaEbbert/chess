package ui;

import org.glassfish.tyrus.core.wsadl.model.Endpoint;

import javax.websocket.*;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {
    private final String socketUrl;
    private Session session;
    public WebSocketFacade(int port) {
        socketUrl = "ws://localhost:" + port;
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
    @OnMessage
    public void onMessage(String message) {
    }
    @OnClose
    public void onClose() {
    }
    @OnOpen
    public void onOpen() {
    }
    @OnError
    public void onError() {
    }
}
