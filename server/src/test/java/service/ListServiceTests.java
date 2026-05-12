package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
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
import static service.RegisterServiceTests.userDAO;

public class ListServiceTests {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final CreateService cService = new CreateService(gameDAO, authDAO);
    static final RegisterService rService = new RegisterService(userDAO, authDAO);
    static final ListService lService = new ListService(gameDAO, authDAO);


    @BeforeEach
    void clear(){
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

@Test
public void listSuccess() throws DataAccessException {
    Auth auth = rService.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
    cService.create(new CreateRequest(auth.authToken(), "mygame"));
    var games = lService.list(new ListRequest(auth.authToken()));
    assertEquals(1, games.size());
}

@Test
public void listUnauthorized() {
    assertThrows(DataAccessException.class, () ->
            lService.list(new ListRequest("fakeToken")));
    }
}