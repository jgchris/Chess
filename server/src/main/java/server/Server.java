package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.JoinRequest;
import model.UserData;
import model.GameData;
import model.AuthData;
import server.websocket.WsHandler;
import service.ChessService;

import java.util.Objects;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {

        service = new ChessService();
        WsHandler handler = new WsHandler(service);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::deleteDb)
            .post("/user", this::register)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .ws("/ws", ws -> {
                ws.onConnect(handler);
                ws.onMessage(handler);
                ws.onClose(handler);
            })
            .exception(Exception.class, this::exceptionHandler);
    }

    private void deleteDb(Context context) {
        service.clear();
    }

    private void register(Context context) throws DataAccessException {
        UserData user = getBodyObject(context, UserData.class);
        if(user.username() == null || user.password() == null || user.email() == null) {
            handle400(context);
            return;
        }
        AuthData auth = service.register(user);

        String json = new Gson().toJson(auth);
        context.json(json);
    }

    private void login(Context context) throws DataAccessException {
        UserData user = getBodyObject(context, UserData.class);
        if(user.username() == null || user.password() == null) {
            handle400(context);
            return;
        }
        AuthData auth = service.login(user);

        String json = new Gson().toJson(auth);
        context.json(json);
    }

    private void logout(Context context) throws DataAccessException {
        AuthData auth = new AuthData(context.header("authorization"), null);

        service.logout(auth);

    }

    private void listGames(Context context) throws DataAccessException {
        AuthData auth = new AuthData(context.header("authorization"), null);

        var games = service.listGames(auth);

        String allGames = new Gson().toJson(games);


        String json = "{\"games\":" + allGames + "}";
        context.json(json);


    }

    private void createGame(Context context) throws DataAccessException {
        AuthData auth = new AuthData(context.header("authorization"), null);
        GameData game = getBodyObject(context, GameData.class);
        if(game.gameName() == null) {
            handle400(context);
            return;
        }

        int id = service.createGame(auth, game.gameName());

        String json = "{\"gameID\":" + id + "}";
        context.json(json);

    }

    private void joinGame(Context context) throws DataAccessException {
        AuthData auth = new AuthData(context.header("authorization"), null);
        JoinRequest request = getBodyObject(context, JoinRequest.class);
        if((request.playerColor() == null) || (request.gameID() == 0)) {
            handle400(context);
            return;
        }

        service.joinGame(auth, request.playerColor(), request.gameID());



    }

    private void exceptionHandler(Exception e, Context context) {
        System.out.println("Error:");
        System.out.println(e);
        if(!(e instanceof DataAccessException)){
            handle500(context, e.getMessage());
            return;
        }

        if(Objects.equals(e.getMessage(), "SQL error") ||
            Objects.equals(e.getMessage(), "failed to get connection")) {
            handle500(context, e.getMessage());
            return;
        }
        if(Objects.equals(e.getMessage(), "Not authorized") ||
                Objects.equals(e.getMessage(), "Auth token not found") ||
                Objects.equals(e.getMessage(), "Incorrect username or password")) {
            handle401(context);
            return;

        }
        if(Objects.equals(e.getMessage(), "playerColor must be WHITE or BLACK")) {
            handle400(context);
            return;
        }
        handle403(context);

    }

    private void handle400(Context context) {
        context.status(400);
        String json = "{\"message\": \"Error: bad request\"}";
        context.json(json);

    }

    private void handle401(Context context) {
        context.status(401);
        String json = "{\"message\": \"Error: unauthorized\"}";
        context.json(json);

    }

    private void handle403(Context context) {
        context.status(403);
        String json = "{\"message\": \"Error: already taken\"}";
        context.json(json);

    }

    private void handle500(Context context, String e) {
        context.status(500);
        String json = "{\"message\": \"Error: " + e + "\"}";
        context.json(json);

    }




    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
