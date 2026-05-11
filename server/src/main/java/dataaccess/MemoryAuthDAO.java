package dataaccess;

import models.Auth;
import models.User;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, Auth> auths = new HashMap<>();

    public MemoryAuthDAO() {
    }

    @Override
    public Auth getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public Auth createAuth(User user) {
        Auth newAuth = new Auth(UUID.randomUUID().toString(), user.username());
        auths.put(newAuth.authToken(), newAuth);
        return newAuth;
    }

    @Override
    public Auth createAuth(String authToken, String username) {
        Auth newAuth = new Auth(authToken, username);
        auths.put(authToken, newAuth);
        return newAuth;
    }

    @Override
    public Auth createAuth(String username) {
        Auth newAuth = new Auth(UUID.randomUUID().toString(), username);
        auths.put(newAuth.authToken(), newAuth);
        return newAuth;
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
