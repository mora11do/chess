package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;

import static org.junit.jupiter.api.Assertions.*;
import static service.RegisterServiceTests.userDAO;

public class LogoutServiceTests {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final RegisterService rService = new RegisterService(userDAO, authDAO);
    static final LogoutService lService = new LogoutService(authDAO);


    @BeforeEach
    void clear() {
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        Auth auth = rService.register(new RegisterRequest("username", "password", "email"));
        assertDoesNotThrow(() ->
                lService.logout(new LogoutRequest(auth.authToken())));
    }

    @Test
    public void logoutUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                lService.logout(new LogoutRequest("fakeToken")));
    }
}