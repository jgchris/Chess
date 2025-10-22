package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import model.GameData;
import model.AuthData;
import service.ChessService;

import java.security.Provider;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {

        service = new ChessService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::deleteDb)
            .post("/user", this::register)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .exception(Exception.class, this::exceptionHandler);
    }

    private void deleteDb(Context context) {
        service.clear();
    }

    private void register(Context context) throws DataAccessException {
        UserData user = getBodyObject(context, UserData.class);
        AuthData auth = service.register(user);

        String json = new Gson().toJson(auth);
        context.json(json);
    }

    private void login(Context context) throws DataAccessException {
        UserData user = getBodyObject(context, UserData.class);
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

        int id = service.createGame(auth, game.gameName());

        String json = "{\"gameID\":" + id + "}";
        context.json(json);

    }

    private void joinGame(Context context) throws DataAccessException {
        AuthData auth = new AuthData(context.header("authorization"), null);



    }

    private void exceptionHandler(Exception e, Context context) {

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
