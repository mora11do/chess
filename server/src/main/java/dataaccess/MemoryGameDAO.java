package dataaccess;

import chess.ChessGame;
import models.Game;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, Game> games = new HashMap<>();
    private int counter = 0;

    public MemoryGameDAO() {
    }

    @Override
    public String createGame(String gameName) {
        counter++;
        Game newGame = new Game(counter,null,null,gameName,new ChessGame());
        games.put(gameName, newGame);
        return gameName;
    }

    @Override
    public Game getGame(String gameName) {
        return games.get(gameName);
    }

    @Override
    public HashMap<String, Game> getAllGames() {
        return games;
    }

    @Override
    public void deleteGame(String gameName) {
        games.remove(gameName);
    }

    @Override
    public void updateGame(Game oldGame, Game newGame) {
        games.put(oldGame.gameName(), newGame);
    }

    @Override
    public void clear() {
        games.clear();
    }
}
