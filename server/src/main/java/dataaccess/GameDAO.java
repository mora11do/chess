package dataaccess;

import models.Game;

import java.util.HashMap;

public interface GameDAO {
    int createGame(String gameName);
    Game getGame(String gameName);
    Game getGame(int gameID);
    HashMap<String, Game> getAllGames();
    void updateGame(Game oldGame, Game newGame);
    void clear();
}
