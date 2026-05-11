package dataaccess;

import chess.ChessGame;
import models.Game;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, Game> gamesAccessByName = new HashMap<>();
    private final HashMap<Integer, Game> gamesAccessByID = new HashMap<>();
    private int counter = 0;

    public MemoryGameDAO() {
    }

    @Override
    public int createGame(String gameName) {
        counter++;
        Game newGame = new Game(counter,null,null,gameName,new ChessGame());
        gamesAccessByName.put(gameName, newGame);
        gamesAccessByID.put(newGame.gameID(), newGame);
        return counter;
    }

    @Override
    public Game getGame(String gameName) {
        return gamesAccessByName.get(gameName);
    }

    @Override
    public Game getGame(int gameID) {
        return gamesAccessByID.get(gameID);
    }

    @Override
    public HashMap<String, Game> getAllGames() {
        return gamesAccessByName;
    }

    @Override
    public void deleteGame(String gameName) {
        int gameID = gamesAccessByName.get(gameName).gameID();
        gamesAccessByName.remove(gameName);
        gamesAccessByID.remove(gameID);
    }

    @Override
    public void updateGame(Game oldGame, Game newGame) {
        gamesAccessByName.put(oldGame.gameName(), newGame);
        gamesAccessByID.put(oldGame.gameID(), newGame);
    }

    @Override
    public void clear() {
        gamesAccessByName.clear();
        gamesAccessByID.clear();
    }
}
