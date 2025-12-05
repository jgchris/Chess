package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage{
    private GameData game;
    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }
}
