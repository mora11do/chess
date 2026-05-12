package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import models.Auth;
import models.CreateRequest;
import models.ListRequest;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.CreateService;
import services.ListService;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class ListServiceTests {
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final CreateService C_SERVICE = new CreateService(GAME_DAO, AUTH_DAO);
    static final RegisterService R_SERVICE = new RegisterService(USER_DAO, AUTH_DAO);
    static final ListService L_SERVICE = new ListService(GAME_DAO, AUTH_DAO);


    @BeforeEach
    void clear(){
        GAME_DAO.clear();
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

@Test
public void listSuccess() throws DataAccessException {
    Auth auth = R_SERVICE.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
    C_SERVICE.create(new CreateRequest(auth.authToken(), "mygame"));
    var games = L_SERVICE.list(new ListRequest(auth.authToken()));
    assertEquals(1, games.size());
}

@Test
public void listUnauthorized() {
    assertThrows(DataAccessException.class, () ->
            L_SERVICE.list(new ListRequest("fakeToken")));
    }
}