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
    }

    @Override
    public AuthData createAuth(String user) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user);

        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    }
}
