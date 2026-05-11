package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.Auth;
import models.RegisterRequest;


public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth register(RegisterRequest request) throws DataAccessException {
        String username = request.username();
        String password = request.password();
        String email = request.email();
        if (userDAO.getUser(username) == null){
            userDAO.createUser(username, password, email);
            return authDAO.createAuth(username);
        }
        else{
            throw new DataAccessException("User with username already exists");
        }
    }
}
