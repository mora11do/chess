package dataaccess;

import models.Game;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, Game> games = new HashMap<>();

    public MemoryGameDAO() {
    }

    @Override
    public void createGame(Game game) {
        games.put(game.gameName(), game);
    }

    @Override
    public Game getGame(String gameName) {
        return games.get(gameName);
    }

    @Override
    public void deleteGame(String gameName) {
        games.remove(gameName);
    }

    @Override
    public void updateGame(Game oldGame, Game newGame) {
        games.put(oldGame.gameName(), newGame);
    }
}
