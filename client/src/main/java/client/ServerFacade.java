package client;

import model.*;

import java.net.http.HttpClient;
import java.util.Collection;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static AuthData register(UserData user) throws ServerError {

    }
    public static AuthData login(UserData user) throws ServerError {

    }
    public static void logout(UserData user) throws ServerError {

    }
    public static Collection<GameData> listGames(AuthData auth) throws ServerError {

    }
    public static int createGame(AuthData auth) throws ServerError {

    }
    public static void joinGame(AuthData auth, String playerColor, int gameId) throws ServerError {

    }

}
