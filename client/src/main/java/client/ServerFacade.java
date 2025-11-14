package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;

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
    public ServerFacade(int port) {
        prefixUrl = String.format("http://localhost:%d", port);
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
            throw new RuntimeException(e);
        }
    }
    private HttpRequest.BodyPublisher requestBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

}
