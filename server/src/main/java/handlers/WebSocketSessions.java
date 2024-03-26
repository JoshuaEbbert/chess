package handlers;



import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

public class WebSocketSessions {
    private HashMap<Integer, HashMap<String, Session>> sessions;

    public WebSocketSessions() {
        sessions = new HashMap<Integer, HashMap<String, Session>>();
    }

    public void addSession(int gameID, String authtoken, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, new HashMap<String, Session>());
        }
        sessions.get(gameID).put(authtoken, session);
    }

    public void removeSessionFromGame(int gameID, String authtoken, Session session) {
        if (sessions.containsKey(gameID)) {
            sessions.get(gameID).remove(authtoken, session);
        }
    }

    public void removeSession(Session session) {
        for (HashMap<String, Session> gameSessions : sessions.values()) {
            gameSessions.values().removeIf(s -> s.equals(session));
        }
    }

    public HashMap<String, Session> getSessionsForGameID(int gameID) {
        return sessions.get(gameID);
    }
}
