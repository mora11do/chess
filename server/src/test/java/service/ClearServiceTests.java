package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import models.Auth;
import models.LoginRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.ClearService;
import services.LoginService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
    static final ClearService C_SERVICE = new ClearService(GAME_DAO, AUTH_DAO,USER_DAO);
    static final LoginService L_SERVICE = new LoginService(USER_DAO, AUTH_DAO);



    @BeforeEach
    void clear(){
        USER_DAO.clear();
        AUTH_DAO.clear();
        GAME_DAO.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password","email"));
        C_SERVICE.clear();
        assertThrows(DataAccessException.class, () ->
                L_SERVICE.login(new LoginRequest("username", "password")));
    }
}