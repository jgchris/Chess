package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authTable = new HashMap<>();
    @Override
    public void clear() {
        authTable.clear();
    }

    @Override
    public AuthData createAuth(String user) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user);
        authTable.put(token, auth);

        return auth;
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
