package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.Auth;
import models.LogoutRequest;
import models.User;


public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public Auth logout(LogoutRequest request) throws DataAccessException {
        String authToken = request.authToken();
        Auth existingAuth = authDAO.getAuth(authToken);
        if (existingAuth != null){
            authDAO.deleteAuth(authToken);
            return existingAuth;
        }
        else{
            throw new DataAccessException("Auth token does not exist");
        }
    }
}
