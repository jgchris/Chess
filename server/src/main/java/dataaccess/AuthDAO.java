package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clear();
    public AuthData createAuth(String user);
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
}
