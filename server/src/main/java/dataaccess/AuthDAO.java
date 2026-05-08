package dataaccess;

import models.Authtoken;

public interface AuthDAO {
    void createAuthToken(Authtoken authToken);
    Authtoken getAuthToken(Authtoken authToken);
    void deleteAuthToken(Authtoken authToken);
}
