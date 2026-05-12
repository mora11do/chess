package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
    static final LogoutService L_SERVICE = new LogoutService(AUTH_DAO);


    @BeforeEach
    void clear() {
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password", "email"));
        assertDoesNotThrow(() ->
                L_SERVICE.logout(new LogoutRequest(auth.authToken())));
    }

    @Test
    public void logoutUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                L_SERVICE.logout(new LogoutRequest("fakeToken")));
    }
}