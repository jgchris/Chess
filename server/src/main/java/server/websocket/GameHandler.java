package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.ChessService;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class GameHandler {
    private int gameId;
    private ChessService service;
    private ConnectionManager connections;
    public GameHandler(int gameId, ChessService service) {
        this.gameId = gameId;
        this.service = service;
        this.connections = new ConnectionManager();
    }

    public void connect(Session session, String token) {
        connections.add(session);
        String username;
        boolean observer = false;
        String playerColor = "";
        GameData game;
        try {
            username = service.getUsername(token);
            game = service.getGame(token, gameId);
            String color = service.getColorOrObserver(token, game);
            if (Objects.equals(color, "Observer")) {
                observer = true;
            } else {
                playerColor = color;
            }
        } catch (DataAccessException e) {
            sendError(session, e.getMessage());
            return;
        }
        ServerMessage message = new LoadGameMessage(game);
        String body = new Gson().toJson(message);
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(body);
            } catch (IOException e) {
                sendError(session, "Failed to send game information");
            }
        }
        String notify;
        if (observer) {
            notify = username + " is now observing the game";
        } else {
            notify = username + " joined the game as " + playerColor;
        }
        ServerMessage connectMessage = new NotificationMessage(notify);
        try {
            connections.broadcast(session, connectMessage);
        } catch (IOException e) {
            sendError(session, "Failed to notify others");
        }

    }

    public void makeMove(Session session, String commandJson) {
        MakeMoveCommand command = new Gson().fromJson(commandJson, MakeMoveCommand.class);
        ChessMove move = command.getMove();
        if (move == null) {
            sendError(session, "Move not included");
            return;
        }
        GameData game;
        try {
            AuthData auth = new AuthData(command.getAuthToken(), null);
            game = service.makeMove(auth, move, gameId);
        } catch (DataAccessException e) {
            sendError(session, e.getMessage());
            return;
        }
        String username = null;
        String otherUsername = null;
        ChessGame.TeamColor otherColor = null;
        try {
            username = service.getUsername(command.getAuthToken());
            otherUsername = service.getUsername(command.getAuthToken());
        } catch (DataAccessException e) {
            sendError(session, e.getMessage());
            return;
        }
        String check = checkCheck(game.game(), otherColor);
        ServerMessage checkNotification = null;
        if (check != null) {
            String checkMessage = otherUsername + " is in " + check;
            checkNotification = new NotificationMessage(checkMessage);
        }
        ServerMessage moveNotification = new LoadGameMessage(game);
        ServerMessage notification = new NotificationMessage(username + " moved " + move.toString());
        try {
            connections.broadcast(null, moveNotification);
            if (checkNotification != null) {
                connections.broadcast(null, checkNotification);
            }
            connections.broadcast(session, notification);
        } catch (IOException e) {
            sendError(session, "Couldn't notify others of move");
        }


    }
    private String checkCheck(ChessGame game, ChessGame.TeamColor color) {
        boolean Check = game.isInCheck(color);
        if (Check) {
            return "Check";
        }
        boolean Checkmate = game.isInCheckmate(color);
        if (Checkmate) {
            game.endGame();
            return "Checkmate. Game over";
        }
        boolean Stalemate = game.isInStalemate(color);
        if (Stalemate) {
            game.endGame();
            return "Stalemate. Game over";
        }
        return null;
    }

    public void resign(Session session, UserGameCommand command) {
        try {
            AuthData auth = new AuthData(command.getAuthToken(), null);
            service.resign(auth, gameId);
            String username = service.getUsername(command.getAuthToken());
            ServerMessage notification = new NotificationMessage(username + " Resigned");
            connections.broadcast(null, notification);
        } catch (DataAccessException e) {
            sendError(session, e.getMessage());
        } catch (IOException e) {
            sendError(session, "Couldn't notify others of resignation");
        }

    }

    public void leave(Session session, UserGameCommand command) {
        connections.remove(session);
        session.close();
        String token = command.getAuthToken();
        String username;
        GameData game = null;
        String color;
        try {
            game = service.getGame(token, gameId);
            color = service.getColorOrObserver(token, game);
        } catch (DataAccessException e) {
            sendError(session, "Could not leave game");
            return;
        }
        if (!Objects.equals(color, "Observer")) {
            AuthData auth = new AuthData(token, null);
            try {
                service.leaveGame(auth, gameId);
            } catch (DataAccessException e) {
                sendError(session, "Could not leave game");
                return;
            }
        }
        try {
            username = service.getUsername(token);
        } catch (DataAccessException e) {
            sendError(session, "Could not notify others in game that you left");
            return;
        }

        ServerMessage notification = new NotificationMessage(username + " left the game");
        try {
            connections.broadcast(session, notification);
        } catch (IOException e) {
            sendError(session, "Could not notify others in game that you left");
        }
    }

    public static void sendError(Session session, String errorMessage) {
        String er = "Error: " + errorMessage;
        ServerMessage message = new ErrorMessage(er);
        String body = new Gson().toJson(message);
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(body);
                return;
            } catch (IOException ignored) {
            }
        }
        System.out.println("Error sending error message to client");
    }
}
