package client;

import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.util.Scanner;

public class ServerMessageObserver {
    public void handleMessage(String message) {
        ServerMessage messageType = new Gson().fromJson(message, ServerMessage.class);
        switch (messageType.getServerMessageType()) {
            case ERROR -> handleError(message);
            case LOAD_GAME -> handleGame(message);
            case NOTIFICATION -> handleNotify(message);
        }

    }
    private void handleError(String message) {
        ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
        String errorMessage = error.getErrorMessage();


    }
    private void handleNotify(String message) {
        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
        String notificationMessage = notification.getMessage();

    }
    private void handleGame(String message) {
        //TODO: does game deserialize correctly?
        LoadGameMessage gameInfo = new Gson().fromJson(message, LoadGameMessage.class);
        GameData game = gameInfo.getGame();

    }

    public String getInputNotificationSafe(String prompt) {
        System.out.println();
        System.out.printf(prompt + " >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
        //TODO: do
    }

}
