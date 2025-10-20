package dataaccess;

import model.UserData;

interface UserDAO {
    public boolean clear();
    public boolean createUser(UserData user);
    public UserData getUser(String username);
}
