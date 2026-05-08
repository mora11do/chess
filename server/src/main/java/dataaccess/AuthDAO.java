package dataaccess;

import models.Authtoken;

public interface AuthDAO {
    void createAuthToken(Authtoken authToken);
    Authtoken getAuthToken(Authtoken authToken);
    Authtoken getAuthToken(String username);
    void deleteAuthToken(Authtoken authToken);
    void deleteAuthToken(String username);
}
