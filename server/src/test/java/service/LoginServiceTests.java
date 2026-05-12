package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import models.Auth;
import models.LoginRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.LoginService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    static final MemoryUserDAO userDAO = new MemoryUserDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final RegisterService rService = new RegisterService(userDAO, authDAO);
    static final LoginService lService = new LoginService(userDAO, authDAO);


    @BeforeEach
    void clear(){
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        Auth auth = rService.register(new RegisterRequest("username", "password", "email"));
        assertDoesNotThrow(() ->
                lService.login(new LoginRequest("username", "password")));
    }

    @Test
    void loginFail() {
        assertThrows(DataAccessException.class, () -> {
            lService.login(new LoginRequest("wrongUsername", "wrongPassword"));
        });
    }
}