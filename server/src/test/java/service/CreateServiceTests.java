package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import models.Auth;
import models.CreateRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CreateService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;
import static service.ClearServiceTests.gameDAO;
import static service.RegisterServiceTests.userDAO;

public class CreateServiceTests {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final CreateService cService = new CreateService(gameDAO, authDAO);
    static final RegisterService rService = new RegisterService(userDAO, authDAO);


    @BeforeEach
    void clear(){
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void createSuccess() throws DataAccessException {
        Auth auth = rService.register(new RegisterRequest("username", "password", "email"));
        int gameID = cService.create(new CreateRequest(auth.authToken(), "game"));
        assertNotNull(gameID);
    }

    @Test
    public void createUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                cService.create(new CreateRequest("fakeToken", "game")));
    }
}