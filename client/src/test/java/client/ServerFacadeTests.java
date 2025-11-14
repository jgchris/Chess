package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private final UserData user = new UserData("test", "test1", "test@example.com");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        clear();
        server.stop();
    }

    @BeforeEach
    void prep() {
        clear();
    }
    static void clear() {
        var url = "http://localhost:8080/db";
        facade.sendRequest(url, "DELETE", null, null);
    }

    private String registerUser() {
        AuthData auth = null;
        try {
            auth = facade.register(user);
        } catch (ServerError e) {
            Assertions.fail("Registering a user threw an exception");
        }
        Assertions.assertNotNull(auth, "Registering returned null");
        Assertions.assertEquals("test", auth.username());

        return auth.authToken();

    }

    private int makeGame(String token) {
        try {
            return facade.createGame(token, "TEST");
        } catch (ServerError e) {
            System.out.println(e);
            Assertions.fail("Creating game failed");
        }
        return 0;
    }

    @Test
    public void testRegisterSuccess() {
        registerUser();
    }
    @Test
    public void testRegisterFail() {
        registerUser();
        Assertions.assertThrows(ServerError.class, () -> {
            facade.register(user);
        });
    }
    @Test
    public void testLoginSuccess() {
        registerUser();
        AuthData auth = null;
        try {
            auth = facade.login(user);
        } catch (ServerError e) {
            Assertions.fail("Login failed");
        }
        Assertions.assertNotNull(auth, "Logging in returned null");
        Assertions.assertEquals("test", auth.username());
    }
    @Test
    public void testLoginFail() {
        Assertions.assertThrows(ServerError.class, () -> {
            facade.login(user);
        });
    }
    @Test
    public void testLogoutSuccess() {
        String token = registerUser();
        try {
            facade.logout(token);
        } catch (ServerError e) {
            Assertions.fail("Logout failed");
        }
    }
    @Test
    public void testLogoutFail() {
        Assertions.assertThrows(ServerError.class, () -> {
            facade.logout("fakeToken");
        });

    }
    @Test
    public void testListGamesSuccess() {
        String auth = registerUser();
        try {
            Collection<GameData> games =  facade.listGames(auth);
            Assertions.assertEquals(0, games.size());
        } catch (ServerError e) {
            Assertions.fail("Failed to list games");
        }
        int gameId = makeGame(auth);
        Assertions.assertEquals(1, gameId);
        try {
            Collection<GameData> games =  facade.listGames(auth);
            Assertions.assertEquals(1, games.size());
            Assertions.assertEquals(GameData.class, games.toArray()[0].getClass());
        } catch (ServerError e) {
            Assertions.fail("Failed to list games");
        }
        int gameId2 = makeGame(auth);
        Assertions.assertEquals(2, gameId2);
        try {
            Collection<GameData> games =  facade.listGames(auth);
            Assertions.assertEquals(2, games.size());
        } catch (ServerError e) {
            Assertions.fail("Failed to list games");
        }
    }
    @Test
    public void testListGamesFail() {
        Assertions.assertThrows(ServerError.class, () -> {
            facade.listGames("fakeToken");
        });
    }
    @Test
    public void testCreateGameSuccess() {
        String token = registerUser();
        int id = makeGame(token);
        Assertions.assertEquals(1, id);
    }
    @Test
    public void testCreateGameFail() {
        Assertions.assertThrows(ServerError.class, () -> {
            facade.createGame("fakeToken", "TEST");
        });
    }
    @Test
    public void testJoinGameSuccess() {
        String token = registerUser();
        int id = makeGame(token);
        try {
            facade.joinGame(token, "WHITE", id);
        } catch (ServerError e) {
            Assertions.fail("Failed to join game");
        }
    }
    @Test
    public void testJoinGameFail() {
        String token = registerUser();
        int id = makeGame(token);
        try {
            facade.joinGame(token, "WHITE", id);
        } catch (ServerError e) {
            Assertions.fail("Failed to join game");
        }
        Assertions.assertThrows(ServerError.class, () -> {
            facade.joinGame(token, "WHITE", id);
        });

    }

}
