package dataaccess;

import models.User;

public interface UserDAO {
    void createUser(User user);
    User getUser(String username);
    User getUser(User user);
    void deleteUser(String username);
    void clear();
}
