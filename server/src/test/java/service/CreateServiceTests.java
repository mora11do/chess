package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import models.Auth;
import models.CreateRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CreateService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;
import static service.RegisterServiceTests.USER_DAO;

public class CreateServiceTests {
    static final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final CreateService C_SERVICE = new CreateService(GAME_DAO, AUTH_DAO);
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);


    @BeforeEach
    void clear(){
        GAME_DAO.clear();
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

    @Test
    public void createSuccess() throws DataAccessException {
        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password", "email"));
        int gameID = C_SERVICE.create(new CreateRequest(auth.authToken(), "game"));
        assertNotNull(gameID);
    }

    @Test
    public void createUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                C_SERVICE.create(new CreateRequest("fakeToken", "game")));
    }
}