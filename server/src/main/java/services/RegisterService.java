package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.Auth;


public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth register(String username, String password, String email) throws DataAccessException {
        if (userDAO.getUser(username) == null){
            userDAO.createUser(username, password, email);
            return authDAO.createAuth(username);
        }
        else{
            throw new DataAccessException("User with username already exists");
        }
    }
}
