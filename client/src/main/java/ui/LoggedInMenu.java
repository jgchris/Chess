package ui;

import java.util.Objects;

public class LoggedInMenu {
    private final String username;
    private final String token;
    private final String prompt;
    public LoggedInMenu(String username, String token) {
        this.username = username;
        this.token = token;
        this.prompt = "[CHESS (" + username + ")]";
    }

    public void loop() {
        while(true) {
            String action = Menu.getInput(this.prompt);
            if (Objects.equals(action, "help")) {
                help();
            } else if (Objects.equals(action, "logout")) {
                break;
            } else if (Objects.equals(action, "create")) {

            } else if (Objects.equals(action, "list")) {

            } else if (Objects.equals(action, "play")) {

            } else if (Objects.equals(action, "observe")) {

            } else{
                System.out.println("Enter 'help' for instructions on how to use the program");
            }
        }
    }

    private void help() {
        System.out.println("create <NAME> — Create a game");
        System.out.println("list — List all ongoing games");
        System.out.println("join <ID> [WHITE|BLACK] — Join a game by game number");
        System.out.println("observe <ID> — Watch a game by game number");
        System.out.println("logout — Log out");
        System.out.println("help — Print this menu");
    }
}
