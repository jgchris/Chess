package client;

import chess.ChessGame;
import model.GameData;
import ui.GameMenu;
import ui.Menu;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class ServerMessageObserver {
    private PipedOutputStream interrupter;
    private final String killSwitch = "##_interrupt_##";
    private final ChessGame.TeamColor color;
    private boolean running = true;
    private GameMenu game;
    public void handleMessage(String message) {
        ServerMessage messageType = new Gson().fromJson(message, ServerMessage.class);
        switch (messageType.getServerMessageType()) {
            case ERROR -> handleError(message);
            case LOAD_GAME -> handleGame(message);
            case NOTIFICATION -> handleNotify(message);
        }

    }
    public ServerMessageObserver(ChessGame.TeamColor color, GameMenu game) {
        this.color = color;
        this.game = game;
    }
    private void handleError(String message) {
        ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
        String errorMessage = error.getErrorMessage();
        System.out.println();
        System.out.println(errorMessage);
        sendKillSwitch();

    }
    private void handleNotify(String message) {
        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
        String notificationMessage = notification.getMessage();
        System.out.println();
        System.out.println(notificationMessage);
        sendKillSwitch();

    }
    private void handleGame(String message) {
        LoadGameMessage gameInfo = new Gson().fromJson(message, LoadGameMessage.class);
        GameData game = gameInfo.getGame();
        Menu.printBoard(game.game().getBoard(), color);
        this.game.updateGame(game);
        sendKillSwitch();
    }
    
    public String getInputNotificationSafe(String prompt) {
        interrupter = new PipedOutputStream();
        PipedInputStream inputStream = null;
        try {
            inputStream = new PipedInputStream(interrupter);
        } catch (IOException e) {
            return null;
        }
        running = true;
        Thread inputForwarder = new Thread(() -> {
            try {
                while (running) {
                    if (System.in.available() > 0) {
                        int data = System.in.read();
                        interrupter.write(data);
                        interrupter.flush();
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (IOException | InterruptedException ignored) {
            }
        });

        inputForwarder.setDaemon(true);
        inputForwarder.start();

        System.out.println();
        System.out.printf(prompt + " >>> ");
        Scanner scanner = new Scanner(inputStream);
        String input = scanner.nextLine();
        if (input.endsWith(killSwitch)) {
            return null;
        }
        return input;
    }
    public void close() {
        running = false;
        try {
            interrupter.close();
        } catch (IOException ignored) {
        }

    }

    private void sendKillSwitch() {
        if (interrupter == null) {
            return;
        }
        String killer = killSwitch + "\n";
        try {
            interrupter.write(killer.getBytes());
        } catch (IOException ignored) {
        }
    }

}
