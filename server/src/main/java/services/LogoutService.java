package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.Auth;
import models.LogoutRequest;
import models.User;


public class LogoutService extends GenericService{

    public LogoutService(AuthDAO authDAO) {
        super(authDAO);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        String authToken = request.authToken();
        if (authIsReal(authToken)) {
            authDAO.deleteAuth(authToken);
            return;
        }
        else{
            throw new DataAccessException("Error: Auth token does not exist", 401);
        }
    }
}
