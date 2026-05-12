package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
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

public class JoinServiceTests {
    static final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final CreateService C_SERVICE = new CreateService(GAME_DAO, AUTH_DAO);
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
    static final JoinService J_SERVICE = new JoinService(GAME_DAO, AUTH_DAO);


    @BeforeEach
    void clear() {
        GAME_DAO.clear();
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

    @Test
    public void joinSuccess() throws DataAccessException {
        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password", "email"));
        int gameID = C_SERVICE.create(new CreateRequest(auth.authToken(), "game"));
        assertDoesNotThrow(() ->
                J_SERVICE.join(new JoinRequest(auth.authToken(), "WHITE", gameID)));
    }

    @Test
    public void joinUnauthorized() {
        assertThrows(DataAccessException.class, () ->
                J_SERVICE.join(new JoinRequest("fakeToken", "WHITE", 1234)));
    }
}