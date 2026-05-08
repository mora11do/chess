package services;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import models.Authtoken;
import models.User;

import java.util.Map;
import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    Authtoken register(User user) {
        if (userDAO.getUser(user) != null){
            userDAO.createUser(user);
            authDAO.createAuthToken(user);
        }
        else{
            throw error
        }
    }
}
