import chess.*;
import ui.LoggedOutMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        System.out.println("Type help for options or quit to exit");
        LoggedOutMenu startMenu = new LoggedOutMenu("[CHESS (LOGGED OUT)]");
        startMenu.loop();
        System.out.println("Have a good day!");
    }
}