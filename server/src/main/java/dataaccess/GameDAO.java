package dataaccess;

import models.Game;

import java.util.HashMap;

public interface GameDAO {
    String createGame(String gameName);
    Game getGame(String gameName);
    HashMap<String, Game> getAllGames();
    void deleteGame(String gameName);
    void updateGame(Game oldGame, Game newGame);
    void clear();
}
