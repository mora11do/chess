package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        try {
            connections.get(gameID).add(session);
        }
        catch(NullPointerException e){
            connections.put(gameID, new HashSet<Session>());
            connections.get(gameID).add(session);
        }
    }

    public void remove(Integer gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage serverMessage, Integer gameID) throws IOException {
        String msg = new Gson().toJson(serverMessage);
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastToOne(Session session, ServerMessage serverMessage) throws IOException {
        String msg = new Gson().toJson(serverMessage);
        if (session.isOpen()) {
                session.getRemote().sendString(msg);
            }
        }
}