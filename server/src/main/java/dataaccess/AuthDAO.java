package dataaccess;

import models.Auth;
import models.User;

public interface AuthDAO {
    Auth createAuth(String authToken, String username);
    Auth createAuth(User user);
    Auth createAuth(String username);
    Auth getAuth(String username);
    void deleteAuthAuthToken(Auth auth);
    void deleteAuthUsername(String username);
    void clear();

}
