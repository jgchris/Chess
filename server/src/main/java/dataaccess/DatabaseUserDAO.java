package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO{
    public DatabaseUserDAO() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }

    private UserData retrieveUser(String username) throws DataAccessException{
        UserData user;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    boolean found = rs.next();
                    if (!found) {
                        return null;
                    }
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    user = new UserData(username, password, email);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }
        return user;

    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var createTableStatement = conn.prepareStatement("DELETE FROM user")) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        UserData checkUser = retrieveUser(user.username());
        if(checkUser != null) {
            throw new DataAccessException("Username already taken");
        }
        String clearTextPassword = user.password();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = retrieveUser(username);
        if(user == null) {
            throw new DataAccessException("Username not found");
        }
        return user;
    }

    @Override
    public void verifyUser(String username, String password) throws DataAccessException {
        UserData user = retrieveUser(username);
        if (user == null) {
            throw new DataAccessException("Incorrect username or password");
        }
        String hashedPassword = user.password();
        boolean match = BCrypt.checkpw(password, hashedPassword);
        if (!match) {
            throw new DataAccessException("Incorrect username or password");
        }


    }
}
