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
    static final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final RegisterService SERVICE = new RegisterService(USER_DAO, AUTH_DAO);


    @BeforeEach
    void clear(){
        USER_DAO.clear();
        AUTH_DAO.clear();
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        Auth auth = SERVICE.register(new RegisterRequest("bob", "password", "email"));
        assertEquals("bob", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void registerDuplicateUser() {
        assertThrows(DataAccessException.class, () -> {
            SERVICE.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
            SERVICE.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
        });
    }
}