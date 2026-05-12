package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import models.Auth;
import models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {
    static final MemoryUserDAO userDAO = new MemoryUserDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final RegisterService service = new RegisterService(userDAO, authDAO);


    @BeforeEach
    void clear(){
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        Auth auth = service.register(new RegisterRequest("bob", "password", "email"));
        assertEquals("bob", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void registerDuplicateUser() {
        assertThrows(DataAccessException.class, () -> {
            service.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
            service.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
        });
    }
}