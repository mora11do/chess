package dataaccess;

import models.User;

public interface UserDAO {
    void createUser(User user);
    User getUser(String username);
    void deleteUser(String username);
}
