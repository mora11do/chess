package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.Auth;
import models.LoginRequest;
import models.RegisterRequest;
import models.User;


public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth login(LoginRequest request) throws DataAccessException {
        String username = request.username();
        String password = request.password();
        User existingUser = userDAO.getUser(username);
        if (existingUser != null){
            if (existingUser.password().equals(password)){
            return authDAO.createAuth(username);
            }
            else{
                throw new DataAccessException("Invalid Credentials");
            }
        }
        else{
            throw new DataAccessException("Username does not exist");
        }
    }
}
