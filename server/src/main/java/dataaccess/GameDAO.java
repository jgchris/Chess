package dataaccess;

import model.GameData;

import java.util.Collection;

interface GameDAO {
    public boolean clear();
    public boolean createGame(GameData game);
    public GameData getGame(String gameID);
    public Collection<GameData> listGames();
    public boolean updateGame(GameData game);
}
