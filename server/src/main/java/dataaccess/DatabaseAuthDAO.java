package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseAuthDAO implements AuthDAO{
    public DatabaseAuthDAO() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var createAuthTable = """
            CREATE TABLE  IF NOT EXISTS auth (
                token VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (token)
            )""";

            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }
    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var createTableStatement = conn.prepareStatement("DELETE FROM auth")) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public AuthData createAuth(String user) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (token, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, user);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT token, username FROM auth WHERE token=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var id = rs.getInt("id");
                        var name = rs.getString("name");
                        var type = rs.getString("type");

                        System.out.printf("id: %d, name: %s, type: %s%n", id, name, type);
                    } else {
                        throw new DataAccessException("Not authorized");
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    }
}
