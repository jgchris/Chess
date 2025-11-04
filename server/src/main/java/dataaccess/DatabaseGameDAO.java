package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {
    public DatabaseGameDAO() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var createAuthTable = """
            CREATE TABLE  IF NOT EXISTS game (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                gameInfo TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )""";

            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }

    private GameData retrieveGame(int gameID){
        GameData game = null;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game WHERE gameID=?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    boolean found = rs.next();
                    if (!found) {
                        return null;
                    }
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameInfo = rs.getString("gameInfo");
                    ChessGame chessGame = new Gson().fromJson(gameInfo, ChessGame.class);
                    game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }
        return game;
    }

    private void addGame(GameData game) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameInfo) VALUES(?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE whiteUsername = ?, blackUsername = ?, gameName = ?, gameInfo = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(6, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(7, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(8, game.gameName());
                ChessGame chessGame = game.game();
                String gameString = new Gson().toJson(chessGame);
                preparedStatement.setString(5, gameString);
                preparedStatement.setString(9, gameString);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error setting game information");
        }


    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var createTableStatement = conn.prepareStatement("DELETE FROM game")) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        GameData gameInfo = retrieveGame(game.gameID());
        if(gameInfo != null) {
            throw new DataAccessException("Game already exists");
        }
        addGame(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = retrieveGame(gameID);
        if(game == null){
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while(rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameInfo = rs.getString("gameInfo");
                        ChessGame chessGame = new Gson().fromJson(gameInfo, ChessGame.class);
                        var game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                        games.add(game);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error getting games list",e);
        }
        return games;

    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        GameData gameInfo = retrieveGame(game.gameID());
        if(gameInfo == null){
            throw new DataAccessException("Game not found");
        }
        try {
            addGame(game);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
