package dataaccess;

import models.Authtoken;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, Authtoken> authTokens = new HashMap<>();

    public MemoryAuthDAO() {
    }

    @Override
    public void createAuthToken(Authtoken authToken) {
        authTokens.put(authToken.username(), authToken.authToken());
    }

    @Override
    public Authtoken getAuthToken(Authtoken authToken) {
        return authTokens.get(authToken.username());
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
}
