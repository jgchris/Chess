package dataaccess;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ChessService;

import java.util.Collection;

public class UnitTests {
    private static ChessService service;
    private final UserData user = new UserData("test", "test1", "test@example.com");

    @BeforeAll
    public static void init(){
        service = new ChessService();
    }

    @BeforeEach
    public void setup() {
        service.clear();
    }


    private AuthData registerUser() {
        AuthData auth = null;
        try {
            auth = service.register(user);
        } catch (DataAccessException e) {
            Assertions.fail("Registering a user threw an exception");
        }
        Assertions.assertNotNull(auth, "Registering returned null");
        Assertions.assertEquals("test", auth.username());

        return auth;

    }

    @Test
    public void clearSuccess() {
        registerUser();
        service.clear();
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.login(user);
        });
    }

    @Test
    public void registerSuccess() {
        registerUser();
    }

    @Test
    public void registerFail() {
        registerUser();
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.register(user);
        });
    }

    @Test
    public void loginSuccess(){
        registerUser();
        AuthData auth = null;
        try {
            auth = service.login(user);
        } catch (DataAccessException e) {
            Assertions.fail("Login failed");
        }
        Assertions.assertNotNull(auth, "Logging in returned null");
        Assertions.assertEquals("test", auth.username());
    }

    @Test
    public void loginFail(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.login(user);
        });
    }

    @Test
    public void logoutSuccess(){
        AuthData auth = registerUser();
        try {
            service.logout(auth);
        } catch (DataAccessException e){
            Assertions.fail("Logout failed");
        }
    }

    @Test
    public void logoutFail(){
        AuthData auth = new AuthData("Token", null);
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.logout(auth);
        });
    }

    private int makeGame(AuthData auth) {
        try {
            return service.createGame(auth, "testGame");
        } catch (DataAccessException e) {
            Assertions.fail("Creating game failed");
        }
        return 0;
    }

    @Test
    public void listGamesSuccess() {
        AuthData auth = registerUser();
        try {
            Collection<GameData> games =  service.listGames(auth);
            Assertions.assertEquals(0, games.size());
        } catch (DataAccessException e) {
            Assertions.fail("Failed to list games");
        }
        int gameId = makeGame(auth);
        Assertions.assertEquals(1, gameId);
        try {
            Collection<GameData> games =  service.listGames(auth);
            Assertions.assertEquals(1, games.size());
        } catch (DataAccessException e) {
            Assertions.fail("Failed to list games");
        }
        int gameId2 = makeGame(auth);
        Assertions.assertEquals(2, gameId2);
        try {
            Collection<GameData> games =  service.listGames(auth);
            Assertions.assertEquals(2, games.size());
        } catch (DataAccessException e) {
            Assertions.fail("Failed to list games");
        }
    }

    @Test
    public void listGamesFail() {
        AuthData auth = new AuthData("Token", null);
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.listGames(auth);
        });
    }

    @Test
    public void createGameSuccess() {
        AuthData auth = registerUser();
        int id = makeGame(auth);
        Assertions.assertEquals(1, id);
    }

    @Test
    public void createGameFail() {
        AuthData auth = new AuthData("Token", null);
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.createGame(auth, "game");
        });
    }

    @Test
    public void joinGameSuccess() {
        AuthData auth = registerUser();
        int id = makeGame(auth);
        try {
            service.joinGame(auth, "WHITE", id);
        } catch (DataAccessException e) {
            Assertions.fail("Failed to join game");
        }
    }

    @Test
    public void joinGameFail() {
        AuthData auth = registerUser();
        int id = makeGame(auth);
        try {
            service.joinGame(auth, "WHITE", id);
        } catch (DataAccessException e) {
            Assertions.fail("Failed to join game");
        }
        Assertions.assertThrows(DataAccessException.class, () -> {
            service.joinGame(auth, "WHITE", id);
        });


    }






}
