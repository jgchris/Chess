package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.Objects;

public class GameMenu {
    private final String prompt;
    private final boolean observe;
    private final ChessGame.TeamColor color;
    private final ChessGame game;
    private final ServerFacade facade;

    public GameMenu(boolean observe, ChessGame.TeamColor color, GameData game, ServerFacade facade) {
        this.prompt = "[CHESS (Playing " + game.gameName() + ")]";
        this.observe = observe;
        if (observe) {
            this.color = ChessGame.TeamColor.WHITE;
        } else {
            this.color = color;
        }
        this.game = game.game();
        this.facade = facade;
        //TODO: connect to websocket
        //TODO: create ServerMessageObserver
    }
    public void loop() {
        while(true) {
            String action = Menu.getInput(this.prompt);
            int i = action.indexOf(" ");
            String command;
            String arguments = "";
            if (i == -1) {
                command = action;
            } else {
                command = action.substring(0, i);
                arguments = action.substring(i+1);
            }
            if (Objects.equals(command, "help")) {
                help();
            } else if (Objects.equals(command, "leave")) {
                //TODO: remove from game, close websocket connection
                break;
            } else if (Objects.equals(command, "redraw")) {
                redraw();
            } else if (Objects.equals(command, "move")) {
                move();
            } else if (Objects.equals(command, "resign")) {
                resign();
            } else if (Objects.equals(command, "highlight")) {
                highlight();
            } else{
                System.out.println("Enter 'help' for instructions on how to use the program");
            }
        }
    }

    private void resign() {
        if (observe) {
            System.out.println("Cannot resign as an observer");
            return;
        }

    }

    private void highlight() {

    }

    private void move() {
        if (observe) {
            System.out.println("Cannot move as an observer");
            return;
        }
        if (game.getTeamTurn() != color) {
            System.out.println("Wait your turn");
            return;
        }

    }

    private void redraw() {
        Menu.printBoard(game.getBoard(), this.color);

    }

    private void help() {
        System.out.println("redraw — Redraw the board");
        System.out.println("leave — Leave the game");
        System.out.println("move — Make a move");
        System.out.println("resign — Forfeit the game");
        System.out.println("highlight — Highlight legal moves of a selected piece");
        System.out.println("help — Print this menu");
    }

    public void sendNotification(String notification) {
        //TODO: probably move to ServerMessageObserver class
        System.out.println();
        System.out.println(notification);
        System.out.printf(prompt + " >>> ");
    }
}
