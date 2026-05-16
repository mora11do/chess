package dataaccess;

import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {
    static final MySqlUserDAO USER_DAO = new MySqlUserDAO();

    public MySqlUserDAOTests() {
    }

    @BeforeEach
    void clear() {
        USER_DAO.clear();
    }

    @Test
    public void createUserSuccess(){
        USER_DAO.createUser("username", "password", "email");
        User user = USER_DAO.getUser("username");
        assertNotNull(user);
    }

    @Test
    public void usernameTaken(){
        USER_DAO.createUser("username", "password", "email");
        assertThrows(DataAccessSQLException.class, () ->
                USER_DAO.createUser("username", "password", "email"));
    }




    @Test
    public void getUserWithUsernameSuccess(){
        USER_DAO.createUser("username", "password", "email");
        User user = USER_DAO.getUser("username");
        assertNotNull(user);
    }

    @Test
    public void getInvalidUsernameWithUsername(){
        User user = USER_DAO.getUser("userNameIsNotReal");
        assertNull(user);
    }

    @Test
    public void getUserWithUserObjectSuccess(){
        User newUser = new User("username","password","email");
        USER_DAO.createUser(newUser.username(),newUser.password(),newUser.email());
        User user = USER_DAO.getUser(newUser);
        assertNotNull(user);
    }

    @Test
    public void getInvalidUsernameWithUserObject(){
        User user = USER_DAO.getUser(new User("userDoesNotExist","password","email"));
        assertNull(user);
    }



    @Test
    public void clearSuccess(){
        USER_DAO.createUser("username", "password", "email");
        USER_DAO.clear();
        assertNull(USER_DAO.getUser("username"));
    }
}
