package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authTable = new HashMap<>();
    @Override
    public void clear() {
        authTable.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if(authTable.get(auth.authToken()) != null) {
            throw new DataAccessException("How did you do that");
        }
        authTable.put(auth.authToken(), auth);

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if(authTable.get(authToken) == null) {
            throw new DataAccessException("Not authorized");
        }
        return authTable.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(authTable.get(authToken) == null) {
            throw new DataAccessException("Auth token not found");
        }
        authTable.remove(authToken);
    }
}
