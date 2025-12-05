package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerError;
import client.ServerFacade;
import client.ServerMessageObserver;
import model.GameData;

import java.util.Objects;

public class GameMenu {
    private final String prompt;
    private final boolean observe;
    private final ChessGame.TeamColor color;
    private final ChessGame game;
    private final int gameId;
    private final ServerFacade facade;
    private final ServerMessageObserver observer;
    private final String token;

    public GameMenu(boolean observe, ChessGame.TeamColor color, GameData game, ServerFacade facade, String token) throws ServerError {
        this.prompt = "[CHESS (Playing " + game.gameName() + ")]";
        this.observe = observe;
        if (observe) {
            this.color = ChessGame.TeamColor.WHITE;
        } else {
            this.color = color;
        }
        this.game = game.game();
        this.gameId = game.gameID();
        this.facade = facade;
        this.observer = new ServerMessageObserver();
        facade.wsConnect(token, game.gameID(), this.observer);
        this.token = token;

    }
    public void loop() {
        label:
        while(true) {
            String command = observer.getInputNotificationSafe(this.prompt);
            switch (command) {
                case "help":
                    help();
                    break;
                case "leave":
                    break label;
                case "redraw":
                    redraw();
                    break;
                case "move":
                    move();
                    break;
                case "resign":
                    resign();
                    break;
                case "highlight":
                    highlight();
                    break;
                default:
                    System.out.println("Enter 'help' for instructions on how to use the program");
                    break;
            }
        }
        try {
            facade.leave(token, gameId);
        } catch (ServerError e) {
            System.out.println("WARNING: failed to leave game");
        }

    }

    private void resign() {
        if (observe) {
            System.out.println("Cannot resign as an observer");
            return;
        }
        String command = observer.getInputNotificationSafe("Do you really want to resign? (y/n)");
        if (command.equals("y")) {

            try {
                facade.resign(token, gameId);
            } catch (ServerError e) {
                System.out.println("Resignation failed");
                return;
            }

        }
    }

    private void highlight() {
        String command = observer.getInputNotificationSafe("Enter position to highlight");

        ChessPosition highlightPos = ChessPosition.createPositionFromString(command);
        if (highlightPos == null) {
            System.out.println("Not a valid position. Use algebraic notation (eg. a1)");
            return;
        }

        Menu.printBoard(game.getBoard(), this.color, highlightPos);
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
        String command = observer.getInputNotificationSafe("Enter starting position");
        ChessPosition startPos = ChessPosition.createPositionFromString(command);
        if (startPos == null) {
            System.out.println("Not a valid position. Use algebraic notation (eg. a1)");
            return;
        }
        //TODO: check piece at position? Check promotion? Check if valid? Highlight moves?
        command = observer.getInputNotificationSafe("Enter final position");
        ChessPosition endPos = ChessPosition.createPositionFromString(command);
        if (endPos == null) {
            System.out.println("Not a valid position. Use algebraic notation (eg. a1)");
            return;
        }
        //TODO: promotion lol
        ChessMove move = new ChessMove(startPos, endPos, null);
        try {
            facade.makeMove(token, gameId, move);
        } catch (ServerError e) {
            System.out.println("Move failed");
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
}
