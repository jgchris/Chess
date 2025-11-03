package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
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
                gameInfo TEXT NOT NULL
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

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }
}
