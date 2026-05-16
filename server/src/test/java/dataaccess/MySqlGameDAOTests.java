package dataaccess;

import chess.ChessGame;
import models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MySqlGameDAOTests {

    static final MySqlGameDAO GAME_DAO = new MySqlGameDAO();

    public MySqlGameDAOTests() {
    }

    @BeforeEach
    void clear() {
        GAME_DAO.clear();
    }

    @Test
    public void createGameSuccess(){
        GAME_DAO.createGame("gameName");
        Game game = GAME_DAO.getGame("gameName");
        assertNotNull(game);
    }

    @Test
    public void createNullGame(){
        assertThrows(DataAccessSQLException.class, () ->
                GAME_DAO.createGame(null));
    }




    @Test
    public void getGameWithGameNameSuccess(){
        GAME_DAO.createGame("gameName");
        Game game = GAME_DAO.getGame("gameName");
        assertNotNull(game);
    }

    @Test
    public void getInvalidGameWithGameName(){
        Game game = GAME_DAO.getGame("gameNameIsNotReal");
        assertNull(game);
    }

    @Test
    public void getGameWithGameIDSuccess(){
        GAME_DAO.createGame("gameName");
        int gameID = GAME_DAO.getGame("gameName").gameID();
        Game game = GAME_DAO.getGame(gameID);
        assertNotNull(game);
    }

    @Test
    public void getInvalidGameWithGameID(){
        Game game = GAME_DAO.getGame(-1);
        assertNull(game);
    }




    @Test
    public void getAllGamesSuccess(){
        GAME_DAO.createGame("gameName");
        var games = GAME_DAO.getAllGames();
        assertFalse(games.isEmpty());
    }

    @Test
    public void getAllGamesWithNoGames(){
        var games = GAME_DAO.getAllGames();
        assertTrue(games.isEmpty());
    }




    @Test
    public void updateGameSuccess(){
        GAME_DAO.createGame("gameName");
        var game = GAME_DAO.getGame("gameName");
        Game newGame = new Game(100,null,"blackUsername", "gameName", new ChessGame());
        GAME_DAO.updateGame(game, newGame);
        assertEquals("blackUsername",GAME_DAO.getGame("gameName").blackUsername());
    }

    @Test
    public void updateNonExistentGame(){
        Game newGame = new Game(100,null,"blackUsername", "gameName", new ChessGame());
        GAME_DAO.updateGame(newGame, newGame);
        assertNull(GAME_DAO.getGame("If it made it this far, " +
                "the update didn't crash everything"));
    }




    @Test
    public void clearSuccess(){
        GAME_DAO.createGame("gameName");
        GAME_DAO.clear();
        assertNull(GAME_DAO.getGame("gameName"));
    }
}