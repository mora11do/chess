package dataaccess;

import models.Game;

public interface GameDAO {
    void createGame(Game game);
    Game getGame(String gameName);
    void deleteGame(String gameName);
    void updateGame(Game oldGame, Game newGame);
}
