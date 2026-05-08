package dataaccess;

import models.User;

public interface UserDAO {
    void createUser(User user);
    User getUser(User user);
    void deleteUser(User user);
}
