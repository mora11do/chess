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
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
    static final LoginService L_SERVICE = new LoginService(USER_DAO, AUTH_DAO);


    @BeforeEach
    void clear(){
        USER_DAO.clear();
        AUTH_DAO.clear();
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password", "email"));
        assertDoesNotThrow(() ->
                L_SERVICE.login(new LoginRequest("username", "password")));
    }

    @Test
    void loginFail() {
        assertThrows(DataAccessException.class, () -> {
            L_SERVICE.login(new LoginRequest("wrongUsername", "wrongPassword"));
        });
    }
}