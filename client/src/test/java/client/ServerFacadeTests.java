package client;

import dataaccess.DataAccessException;
import models.Auth;
import models.RegisterRequest;
import models.User;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws ResponseException{
        facade.clear();
    }

    @Test
    public void registerSuccess() throws ResponseException{
        Auth auth = facade.register(new User("username","password","email"));
        Assertions.assertNotNull(auth);
    }

    @Test
    public void registerFailure() throws ResponseException{
        assertThrows(ResponseException.class, () -> {
            facade.register(new User("bob", "password", "bob@gmail.com"));
            facade.register(new User("bob", "password", "bob@gmail.com"));
        });
    }

}
