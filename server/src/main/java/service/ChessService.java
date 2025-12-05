package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
    public GameData makeMove(AuthData auth, ChessMove move, int gameId) throws DataAccessException {
        AuthData authInfo = checkAuth(auth.authToken());
        String username = authInfo.username();
        GameData game = gameDAO.getGame(gameId);
        ChessGame chessGame = game.game();
        ChessGame.TeamColor color =  checkPlayerColor(username, game);
        ChessGame.TeamColor teamTurn = game.game().getTeamTurn();
        ChessGame.TeamColor pieceColor;
        try {
            pieceColor = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        } catch (NullPointerException e) {
            throw new DataAccessException("Piece not found");
        }
        if (color != teamTurn) {
            throw new DataAccessException("Not your turn");
        }
        if (color != pieceColor) {
            throw new DataAccessException("Not your piece");
        }
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException("Invalid move");
        }
        GameData newGame = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        gameDAO.updateGame(newGame);
        return newGame;
    }
    public void resign(AuthData auth, int gameId) throws DataAccessException {
        AuthData authInfo = checkAuth(auth.authToken());
        GameData game = gameDAO.getGame(gameId);
        ChessGame.TeamColor isPlayingGame = checkPlayerColor(authInfo.username(), game);
        ChessGame chessGame = game.game();
        if (chessGame.isGameOver()) {
            throw new DataAccessException("Game is already over");
        }
        chessGame.endGame();
        GameData newGame = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        gameDAO.updateGame(newGame);
    }
    public void leaveGame(AuthData auth, int gameId) throws DataAccessException {
        AuthData authInfo = checkAuth(auth.authToken());
        GameData game = gameDAO.getGame(gameId);
        String username = authInfo.username();
        ChessGame.TeamColor color = checkPlayerColor(username, game);
        if(color == ChessGame.TeamColor.WHITE) {
            GameData newGame = new GameData(gameId, null, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(newGame);
            return;
        }
        if(color == ChessGame.TeamColor.BLACK) {
            GameData newGame = new GameData(gameId, game.whiteUsername(), null, game.gameName(), game.game());
            gameDAO.updateGame(newGame);
            return;
        }
    }
    private ChessGame.TeamColor checkPlayerColor(String username, GameData game) throws DataAccessException {
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (Objects.equals(username, white)) {
            return ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(username, black)) {
            return ChessGame.TeamColor.BLACK;
        }
        throw new DataAccessException("Not a player in this game");
    }
    public String getUsername(String token) throws DataAccessException {
        return checkAuth(token).username();
    }
    public String getColorOrObserver(String token, GameData game) throws DataAccessException {
        AuthData authInfo = checkAuth(token);
        String username = authInfo.username();
        try {
            ChessGame.TeamColor color = checkPlayerColor(username, game);
            return switch (color) {
                case WHITE -> "White";
                case BLACK -> "Black";
                case null, default -> "Observer";
            };
        } catch (DataAccessException e) {
            return "Observer";
        }
    }
    public GameData getGame(String token, int gameId) throws DataAccessException{
        checkAuth(token);
        return gameDAO.getGame(gameId);
    }
    public String getColorUsername(String token, int gameId, ChessGame.TeamColor color) throws DataAccessException {
        checkAuth(token).username();
        GameData game = gameDAO.getGame(gameId);
        if (color == ChessGame.TeamColor.WHITE) {
            return game.whiteUsername();
        }
        if (color == ChessGame.TeamColor.BLACK) {
            return game.blackUsername();
        }
        throw new DataAccessException("what");
    }
    public ChessGame.TeamColor getOtherColor(String token, int gameId) throws DataAccessException {
        GameData game = gameDAO.getGame(gameId);
        String myColor = getColorOrObserver(token, game);
        if (Objects.equals(myColor, "WHITE")) {
            return ChessGame.TeamColor.BLACK;
        }
        return ChessGame.TeamColor.WHITE;
    }
}
