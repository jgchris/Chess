package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

interface GameDAO {
    public void clear();
    public void createGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public Collection<GameData> listGames();
    public void updateGame(GameData game) throws DataAccessException;
}

class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameTable = new HashMap<>();
    public void clear() {
        gameTable.clear();
    };
    public void createGame(GameData game) throws DataAccessException {
        if(gameTable.get(game.gameID()) != null){
            throw new DataAccessException("Game already exists");
        }
        gameTable.put(game.gameID(), game);
    };
    public GameData getGame(int gameID) throws DataAccessException{
        GameData game = gameTable.get(gameID);
        if(game == null){
            throw new DataAccessException("Game not found");
        }
        return game;

    };
    public Collection<GameData> listGames() {
        return gameTable.values();
    };
    public void updateGame(GameData game) throws DataAccessException{
        if(gameTable.get(game.gameID()) == null){
            throw new DataAccessException("Game not found");
        }
        gameTable.put(game.gameID(), game);
    };
}
