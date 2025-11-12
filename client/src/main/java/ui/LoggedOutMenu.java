package ui;

import java.util.Objects;

public class LoggedOutMenu {
    private final String prompt;
    public LoggedOutMenu(String prompt) {
        this.prompt = prompt;

    }
    public void loop() {
        while(true){
            String action = Menu.getInput(this.prompt);
            System.out.println(action);
            if(Objects.equals(action, "help")){
                help();
            } else if (Objects.equals(action, "quit")) {
                break;
            } else if (Objects.equals(action, "login")) {

            } else if (Objects.equals(action, "register")) {

            } else{
                System.out.println("Enter 'help' for instructions on how to use the program");
            }

        }
    }

    private void help() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> — Create an account");
        System.out.println("login <USERNAME> <PASSWORD> — Login to created account");
        System.out.println("quit — Exit the program");
        System.out.println("help — Print this screen");
    }
}
