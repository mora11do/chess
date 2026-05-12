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
    static final MemoryUserDAO userDAO = new MemoryUserDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final RegisterService rService = new RegisterService(userDAO, authDAO);
    static final ClearService cService = new ClearService(gameDAO, authDAO,userDAO);
    static final LoginService lService = new LoginService(userDAO, authDAO);



    @BeforeEach
    void clear(){
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        Auth auth = rService.register(new RegisterRequest("username", "password","email"));
        cService.clear();
        assertThrows(DataAccessException.class, () ->
                lService.login(new LoginRequest("username", "password")));
    }
}