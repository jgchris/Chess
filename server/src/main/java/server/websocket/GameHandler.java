package server.websocket;

import service.ChessService;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
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

    public void makeMove(Session session, UserGameCommand command) {

    }

    public void resign(Session session, UserGameCommand command) {

    }

    public void leave(Session session, UserGameCommand command) {
        connections.remove(session);

        connections.broadcast(session, );
        session.close();
    }

    public static void sendError(Session session, String errorMessage) {

    }
}
