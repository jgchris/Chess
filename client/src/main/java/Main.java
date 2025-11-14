import chess.*;
import client.ServerFacade;
import server.Server;
import ui.LoggedOutMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        System.out.println("Type help for options or quit to exit");
        int port = 8080;
        Server server = new Server();
        port = server.run(port);
        ServerFacade facade = new ServerFacade(port);
        LoggedOutMenu startMenu = new LoggedOutMenu("[CHESS (LOGGED OUT)]", facade);
        startMenu.loop();
        System.out.println("Have a good day!");
        server.stop();
    }
}