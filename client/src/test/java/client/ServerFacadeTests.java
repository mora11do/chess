package client;

import dataaccess.DataAccessException;
import models.*;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    public void loginSuccess() throws ResponseException{
        User newUser =  new User("username", "password", "email");
        facade.register(newUser);
        Auth auth = facade.login(newUser);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void loginFailure() throws ResponseException{
        assertThrows(ResponseException.class, () -> {
            facade.login(new User("userDoesNotExist", "password", "email"));;
        });
    }

    @Test
    public void clearSuccess() throws ResponseException {
        User newUser =  new User("username", "password", "email");
        facade.register(newUser);
        facade.clear();
        assertThrows(ResponseException.class, () ->
                facade.login(newUser));
    }

    @Test
    public void createSuccess() throws ResponseException {
        Auth auth = facade.register(new User("username", "password", "email"));
        Assertions.assertDoesNotThrow(() -> facade.create(auth, "gameName"));
    }

    @Test
    public void createUnauthorized() throws ResponseException{
        facade.register(new User("username", "password", "email"));
        assertThrows(ResponseException.class, () ->
                facade.create(new Auth("fakeAuthToken","username"), "gameName"));
    }

    @Test
    public void joinSuccess() throws ResponseException {
        Auth auth = facade.register(new User("username", "password", "email"));
        int gameID = facade.create(auth, "gameName");

        assertDoesNotThrow(() ->
                facade.join(auth, gameID, "WHITE"));
    }

    @Test
    public void joinUnauthorized() {
        assertThrows(ResponseException.class, () ->
                facade.join(new Auth("fakeAuthToken", "username"),1, "WHITE"));
    }
//
//    @Test
//    public void listSuccess() throws DataAccessException {
//        Auth auth = R_SERVICE.register(new RegisterRequest("bob", "password", "bob@gmail.com"));
//        C_SERVICE.create(new CreateRequest(auth.authToken(), "mygame"));
//        var games = L_SERVICE.list(new ListRequest(auth.authToken()));
//        assertEquals(1, games.size());
//    }
//
//    @Test
//    public void listUnauthorized() {
//        assertThrows(DataAccessException.class, () ->
//                L_SERVICE.list(new ListRequest("fakeToken")));
//    }
//
//    @Test
//    public void logoutSuccess() throws DataAccessException {
//        Auth auth = R_SERVICE.register(new RegisterRequest("username", "password", "email"));
//        assertDoesNotThrow(() ->
//                L_SERVICE.logout(new LogoutRequest(auth.authToken())));
//    }
//
//    @Test
//    public void logoutUnauthorized() {
//        assertThrows(DataAccessException.class, () ->
//                L_SERVICE.logout(new LogoutRequest("fakeToken")));
//    }

}
