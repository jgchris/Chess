package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> userTable = new HashMap<>();


    @Override
    public void clear() {
        userTable.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(userTable.get(user.username()) != null) {
            throw new DataAccessException("Username already taken");
        }
        userTable.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = userTable.get(username);
        if(user == null){
            throw new DataAccessException("Username not found");
        }
        return user;
    }

    @Override
    public void verifyUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        if(!password.equals(user.password())){
            throw new DataAccessException("Incorrect username or password");
        }
    }
}
