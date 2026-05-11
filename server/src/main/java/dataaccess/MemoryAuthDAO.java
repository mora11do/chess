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
    public Auth getAuth(String username) {
        return auths.get(username);
    }

    @Override
    public void deleteAuthAuthToken(Auth auth) {
        auths.remove(auth.username());
    }

    @Override
    public void deleteAuthUsername(String username) {
        auths.remove(username);
    }

    @Override
    public Auth createAuth(User user) {
        Auth newAuth = new Auth(UUID.randomUUID().toString(), user.username());
        auths.put(newAuth.username(), newAuth);
        return newAuth;
    }

    @Override
    public Auth createAuth(String authToken, String username) {
        Auth newAuth = new Auth(authToken, username);
        auths.put(username, newAuth);
        return newAuth;
    }

    @Override
    public Auth createAuth(String username) {
        Auth newAuth = new Auth(UUID.randomUUID().toString(), username);
        auths.put(username, newAuth);
        return newAuth;
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
