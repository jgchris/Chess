package server;

import io.javalin.*;
import io.javalin.http.Context;
import service.ChessService;

public class Server {

    private final Javalin javalin;
    private ChessService service;

    public Server() {

        service = new ChessService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::deleteDb)
            .post("/user", this::register)
            .post("/session", this::login)
            .delete("/sessions", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .exception(Exception.class, this::exceptionHandler);
    }

    private void deleteDb(Context context) {
        service.clear();


    }

    private void register(Context context) {

    }

    private void login(Context context) {

    }

    private void logout(Context context) {

    }

    private void listGames(Context context) {

    }

    private void createGame(Context context) {

    }

    private void joinGame(Context context) {

    }

    private void exceptionHandler(Exception e, Context context) {

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
