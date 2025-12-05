package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.ChessService;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

public class WsHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private HashMap<Integer, GameHandler> idToHandler = new HashMap<Integer, GameHandler>();
    private ChessService service;

    public WsHandler(ChessService service) {
        this.service = service;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket connection closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        UserGameCommand.CommandType type = command.getCommandType();
        if (type == UserGameCommand.CommandType.CONNECT) {
            connect(command, ctx.session);
            return;
        }
        GameHandler handler = idToHandler.get(command.getGameID());
        if (handler == null) {
            GameHandler.sendError(ctx.session,"Game not found; please send connect message to websocket");
        }
        switch (type) {
            case LEAVE -> handler.leave(ctx.session, command);
            case RESIGN -> handler.resign(ctx.session, command);
            case MAKE_MOVE -> handler.makeMove(ctx.session, command);
        }
    }

    private void connect(UserGameCommand command, Session session) {
        int gameId = command.getGameID();
        GameHandler handler = idToHandler.get(gameId);
        if (handler == null) {
            handler = new GameHandler(gameId, this.service);
            idToHandler.put(gameId, handler);
        }
        handler.connect(session);
    }
}
