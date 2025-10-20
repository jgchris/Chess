package dataaccess;

import model.AuthData;

interface AuthDAO {
    public boolean clear();
    public boolean createAuth(AuthData auth);
    public AuthData getAuth(String authToken);
    public bool deleteAuth(String authToken);
}
