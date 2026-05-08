package dataaccess;

import models.Game;

public interface GameDAO {
    void createGame(Game game);
    Game getGame(Game game);
    void deleteGame(Game game);
    void updateGame(Game oldGame, Game newGame);
}
