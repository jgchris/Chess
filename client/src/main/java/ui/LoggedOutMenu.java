package ui;

import client.ServerError;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.Objects;

public class LoggedOutMenu {
    private final String prompt;
    private final ServerFacade facade;
    public LoggedOutMenu(String prompt, ServerFacade facade) {
        this.prompt = prompt;
        this.facade = facade;

    }
    public void loop() {
        while(true){
            String action = Menu.getInput(this.prompt);
            int i = action.indexOf(" ");
            String command;
            String arguments = null;
            if (i == -1) {
                command = action;
            } else {
                command = action.substring(0, i);
                arguments = action.substring(i+1);
            }
            if(Objects.equals(command, "help")){
                help();
            } else if (Objects.equals(command, "quit")) {
                break;
            } else if (Objects.equals(command, "login")) {
                login(arguments);
            } else if (Objects.equals(command, "register")) {
                register(arguments);
            } else{
                System.out.println("Enter 'help' for instructions on how to use the program");
            }
        }
    }

    private void login(String arguments) {
        String[] patterns = new String[] {"[a-zA-Z][^\s]*", "^[a-zA-Z][^\s]*$"};
        String[] args = Menu.parseArgs(arguments, patterns, "Usage: login <USERNAME> <PASSWORD>");
        if (args == null) {
            return;
        }
        String username = args[0];
        String password = args[1];

        String token;
        UserData user = new UserData(username, password, null);
        try {
            AuthData auth = facade.login(user);
            token = auth.authToken();
        } catch (ServerError e) {
            System.out.println(e.getMessage());
            return;
        }
        enterLoggedInMenu(token, username);
    }

    private void register(String arguments) {
        String[] patterns = new String[] {"[a-zA-Z][^\s]*", "^[a-zA-Z][^\s]*$", "^[a-zA-Z]+@[a-zA-Z]+.com"};
        String[] args = Menu.parseArgs(arguments, patterns, "Usage: register <USERNAME> <PASSWORD> <EMAIL>");
        if (args == null) {
            return;
        }
        String username = args[0];
        String password = args[1];
        String email = args[2];

        String token;
        UserData user = new UserData(username, password, email);
        try {
            AuthData auth = facade.register(user);
            token = auth.authToken();
        } catch (ServerError e) {
            System.out.println(e.getMessage());
            return;
        }
        enterLoggedInMenu(token, username);
    }

    private void enterLoggedInMenu(String token, String username) {
        LoggedInMenu menu = new LoggedInMenu(username, token, this.facade);
        menu.loop();
        System.out.println("Logged out " + username);

    }

    private void help() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> — Create an account");
        System.out.println("login <USERNAME> <PASSWORD> — Login to created account");
        System.out.println("quit — Exit the program");
        System.out.println("help — Print this screen");
    }
}
