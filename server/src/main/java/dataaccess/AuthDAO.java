package dataaccess;

import models.Authtoken;
import models.User;

public interface AuthDAO {
    Authtoken createAuthToken(Authtoken authToken);
    Authtoken createAuthToken(User user);
    Authtoken getAuthToken(String username);
    void deleteAuthToken(Authtoken authToken);
    void deleteAuthToken(String username);
    void clear();
}
