package dataaccess;

import models.Authtoken;
import models.User;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, String> authTokens = new HashMap<>();

    public MemoryAuthDAO() {
    }

    @Override
    public Authtoken createAuthToken(User user) {
        Authtoken authToken = new Authtoken(user.username(), UUID.randomUUID().toString());
        authTokens.put(user.username(), authToken.uuid());
        return authToken;
    }

    @Override
    public Authtoken createAuthToken(Authtoken authToken) {
        authTokens.put(authToken.username(), authToken.uuid());
        return authToken;
    }

    @Override
    public Authtoken getAuthToken(String username) {
        return authTokens.get(username);
    }

    @Override
    public void deleteAuthToken(Authtoken authToken) {
        authTokens.remove(authToken.username());
    }

    @Override
    public void deleteAuthToken(String username) {
        authTokens.remove(username);
    }

    @Override
    public void clear() {
        authTokens.clear();
    }
}
