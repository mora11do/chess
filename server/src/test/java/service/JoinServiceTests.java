package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import models.Auth;
import models.CreateRequest;
import models.JoinRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CreateService;
import services.JoinService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;
import static service.RegisterServiceTests.userDAO;

public class JoinServiceTests {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final CreateService cService = new CreateService(gameDAO, authDAO);
    static final RegisterService rService = new RegisterService(userDAO, authDAO);
    static final JoinService jService = new JoinService(gameDAO, authDAO);


    @BeforeEach
    void clear() {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void joinSuccess() throws DataAccessException {
        Auth auth = rService.register(new RegisterRequest("username", "password", "email"));
        int gameID = cService.create(new CreateRequest(auth.authToken(), "game"));
        assertDoesNotThrow(() ->
                jService.join(new JoinRequest(auth.authToken(), "WHITE", gameID)));
    }

    @Test
    public void joinUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                jService.join(new JoinRequest("fakeToken", "WHITE", 1234)));
    }
}