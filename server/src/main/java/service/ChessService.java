package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import model.AuthData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

public class ChessService {
    //Change these to use SQL DAOs
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private int currentGameId = 1;

    public ChessService() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database could not be created.", e);
        }
        try {
            authDAO = new DatabaseAuthDAO();
            gameDAO = new DatabaseGameDAO();
            userDAO = new DatabaseUserDAO();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Could not create required tables in database");
        }

    }

    private AuthData checkAuth(String token) throws DataAccessException {
        return authDAO.getAuth(token);
    }
    public void clear(){
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        currentGameId = 1;
    }
    public AuthData register(UserData user) throws DataAccessException {
        userDAO.createUser(user);
        return authDAO.createAuth(user.username());
    }
    public AuthData login(UserData user) throws DataAccessException {
        userDAO.verifyUser(user.username(), user.password());
        return authDAO.createAuth(user.username());
    }
    public void logout(AuthData auth) throws DataAccessException {
        authDAO.deleteAuth(auth.authToken());
    }
    public Collection<GameData> listGames(AuthData auth) throws DataAccessException {
        checkAuth(auth.authToken());
        return gameDAO.listGames();
    }
    public int createGame(AuthData auth, String gameName) throws DataAccessException {
        checkAuth(auth.authToken());
        int gameId = currentGameId;
        currentGameId++;
        ChessGame game = new ChessGame();
        GameData gameInfo = new GameData(gameId, null, null, gameName, game);
        gameDAO.createGame(gameInfo);
        return gameId;
    }
    public void joinGame(AuthData auth, String playerColor, int gameId ) throws DataAccessException {
        AuthData authInfo = checkAuth(auth.authToken());
        GameData game = gameDAO.getGame(gameId);
        if(Objects.equals(playerColor, "WHITE")) {
            if(game.whiteUsername() != null){
                throw new DataAccessException("Color already taken");
            }
            GameData newGame = new GameData(gameId, authInfo.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(newGame);
        } else if (Objects.equals(playerColor, "BLACK")) {
            if(game.blackUsername() != null){
                throw new DataAccessException("Color already taken");
            }
            GameData newGame = new GameData(gameId, game.whiteUsername(), authInfo.username(), game.gameName(), game.game());
            gameDAO.updateGame(newGame);

        } else {
            throw new DataAccessException("playerColor must be WHITE or BLACK");
        }

    }
}
