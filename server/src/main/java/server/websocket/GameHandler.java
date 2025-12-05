package server.websocket;

import server.Server;
import service.ChessService;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public class GameHandler {
    private int gameId;
    private ChessService service;
    private ConnectionManager connections;
    public GameHandler(int gameId, ChessService service) {
        this.gameId = gameId;
        this.service = service;
        this.connections = new ConnectionManager();
    }

    public void connect(Session session) {
        connections.add(session);
    }

    public void makeMove(Session session) {

    }

    public void resign(Session session) {

    }

    public void leave(Session session) {

    }

    public static void sendError(Session session, String errorMessage) {

    }
    public static void sendMessage(Session session, ServerMessage message) {

    }
}
