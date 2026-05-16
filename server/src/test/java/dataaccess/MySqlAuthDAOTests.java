package dataaccess;

import models.Auth;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTests {
    static final MySqlAuthDAO AUTH_DAO = new MySqlAuthDAO();

    public MySqlAuthDAOTests() {
    }

    @BeforeEach
    void clear() {
        AUTH_DAO.clear();
    }

    @Test
    public void createAuthWithAuthTokenUsernameSuccess(){
        AUTH_DAO.createAuth("authToken","username");
        Auth auth = AUTH_DAO.getAuth("authToken");
        assertNotNull(auth);
    }

    @Test
    public void authTokenForUsernameNullThroughAuthTokenUsername(){
        assertThrows(DataAccessSQLException.class, () ->
                AUTH_DAO.createAuth("authToken", null));
    }

    @Test
    public void createAuthWithOnlyUsernameSuccess(){
        Auth auth = AUTH_DAO.getAuth(AUTH_DAO.createAuth("username").authToken());
        assertNotNull(auth);
    }

    @Test
    public void authTokenForUsernameNullThroughOnlyUsername(){
        assertThrows(DataAccessSQLException.class, () ->
                AUTH_DAO.createAuth((String) null));
    }

    @Test
    public void createAuthWithUserSuccess(){
        User user = new User("username","password","email");
        Auth auth = AUTH_DAO.getAuth(AUTH_DAO.createAuth(user).authToken());
        assertNotNull(auth);
    }

    @Test
    public void authTokenForUsernameNullThroughUser(){
        assertThrows(DataAccessSQLException.class, () ->
                AUTH_DAO.createAuth(new User(null, "password", "email")));
    }




    @Test
    public void deleteAuthSuccess(){
        AUTH_DAO.createAuth("authToken","username");
        AUTH_DAO.deleteAuth("authToken");
        assertNull(AUTH_DAO.getAuth("authToken"));
    }

    @Test
    public void deleteNonexistentAuth(){
        assertDoesNotThrow(() -> AUTH_DAO.deleteAuth("fakeToken"));

    }




    @Test
    public void getAuthSuccess(){
        AUTH_DAO.createAuth("authToken","username");
        Auth auth = AUTH_DAO.getAuth("authToken");
        assertNotNull(auth);
    }

    @Test
    public void getNonexistentAuth(){
        assertNull(AUTH_DAO.getAuth("fakeToken"));
    }




    @Test
    public void clearSuccess(){
        AUTH_DAO.createAuth("authToken","username");
        AUTH_DAO.clear();
        assertNull(AUTH_DAO.getAuth("authToken"));
    }
}
