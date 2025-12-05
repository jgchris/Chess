package client;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String prefixUrl;
    private final String websocketUrl;
    private WsFacade wsFacade = null;
    public ServerFacade(int port) {
        prefixUrl = String.format("http://localhost:%d", port);
        this.websocketUrl = String.format("ws://localhost:%d/ws", port);
    }
    public AuthData register(UserData user) throws ServerError {
        var body = new Gson().toJson(user);
        var url = this.prefixUrl + "/user";
        var response = sendRequest(url, "POST", body, null);
        if (response.statusCode() == 200) {
            return new Gson().fromJson(response.body(), AuthData.class);
        }

        var errorBody = new Gson().fromJson(response.body(), HashMap.class);
        String errorMessage = errorBody.get("message").toString();
        throw new ServerError(errorMessage);

    }
    public AuthData login(UserData user) throws ServerError {
        var body = new Gson().toJson(user);
        var url = this.prefixUrl + "/session";
        var response = sendRequest(url, "POST", body, null);
        if (response.statusCode() == 200) {
            return new Gson().fromJson(response.body(), AuthData.class);
        }
        var errorBody = new Gson().fromJson(response.body(), HashMap.class);
        String errorMessage = errorBody.get("message").toString();
        throw new ServerError(errorMessage);

    }
    public void logout(String token) throws ServerError {
        var url = this.prefixUrl + "/session";
        var response = sendRequest(url, "DELETE", null, token);
        if(response.statusCode() > 299) {
            var errorBody = new Gson().fromJson(response.body(), HashMap.class);
            String errorMessage = errorBody.get("message").toString();
            throw new ServerError(errorMessage);
        }
    }
    public Collection<GameData> listGames(String token) throws ServerError {
        var url = this.prefixUrl + "/game";
        var response = sendRequest(url, "GET", null, token);
        if (response.statusCode() == 200) {
            Type type = new TypeToken<Map<String, Collection<GameData>>>(){}.getType();
            Map<String, Collection<GameData>> info= new Gson().fromJson(response.body(), type);
            return info.get("games");
        }
        var errorBody = new Gson().fromJson(response.body(), HashMap.class);
        String errorMessage = errorBody.get("message").toString();
        throw new ServerError(errorMessage);

    }
    public int createGame(String token, String name) throws ServerError {
        var url = this.prefixUrl + "/game";
        GameData game = new GameData(null, null, null, name, null);
        String body = new Gson().toJson(game);
        var response = sendRequest(url, "POST", body, token);
        if (response.statusCode() == 200) {
            var infoBody =  new Gson().fromJson(response.body(), HashMap.class);
            double id = (double) infoBody.get("gameID");
            return (int) id;
        }
        var errorBody = new Gson().fromJson(response.body(), HashMap.class);
        String errorMessage = errorBody.get("message").toString();
        throw new ServerError(errorMessage);

    }
    public void joinGame(String token, String playerColor, int gameId) throws ServerError {
        var url = this.prefixUrl + "/game";
        JoinRequest request = new JoinRequest(playerColor, gameId);
        var body = new Gson().toJson(request);
        var response = sendRequest(url, "PUT", body, token);
        if (response.statusCode() != 200) {
            var errorBody = new Gson().fromJson(response.body(), HashMap.class);

            String message = errorBody.get("message").toString();
            throw new ServerError(message);
        }

    }
    public HttpResponse<String> sendRequest(String url, String method, String body, String token) {
        HttpRequest request;
        if (token == null) {
            request = HttpRequest.newBuilder(URI.create(url))
                    .method(method, requestBodyPublisher(body))
                    .build();
         } else {
            request = HttpRequest.newBuilder(URI.create(url))
                    .method(method, requestBodyPublisher(body))
                    .header("authorization", token)
                    .build();
        }
        try {
            return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not connect to server.");
            System.out.println("Make sure server is running on the correct port.");
            return null;
        }
    }
    private HttpRequest.BodyPublisher requestBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public void wsConnect(String token, int gameId, ServerMessageObserver observer) throws ServerError{
        try {
            this.wsFacade = new WsFacade(websocketUrl, observer);
        } catch (Exception e) {
            throw new ServerError("Could not connect to Websocket");
        }
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, token, gameId);
        var body = new Gson().toJson(command);
        try {
            this.wsFacade.send(body);
        } catch (IOException e) {
            throw new ServerError("Failure connecting to game");
        }

    }

    public void makeMove(String token, int gameId, ChessMove move) throws ServerError {
        if (wsFacade == null) {
            System.out.println("Connection lost. Try leaving and rejoining the game.");
        }
        UserGameCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, token, gameId, move);
        String message = new Gson().toJson(command);
        try {
            wsFacade.send(message);
        } catch (IOException e) {
            throw new ServerError("Failed to make move");
        }
    }
    public void resign(String token, int gameId) throws ServerError {
        if (wsFacade == null) {
            System.out.println("Connection lost. Try leaving and rejoining the game.");
        }
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, token, gameId);
        String message = new Gson().toJson(command);
        try {
            wsFacade.send(message);
        } catch (IOException e) {
            throw new ServerError("Failed to resign");
        }
    }
    public void leave(String token, int gameId) throws ServerError{
        if (wsFacade == null) {
            System.out.println("Connection lost. Try leaving and rejoining the game.");
        }
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, token, gameId);
        String message = new Gson().toJson(command);
        try {
            wsFacade.send(message);
        } catch (IOException e) {
            throw new ServerError("Failed to leave game");
        }
    }

}
