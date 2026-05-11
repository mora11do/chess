package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.*;

import java.util.ArrayList;
import java.util.HashMap;


public class ListService extends GenericService {
    private final GameDAO gameDAO;

    public ListService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;

    }

    public ArrayList<GameWithNoChessGame> list(ListRequest request) throws DataAccessException {
        String authToken = request.authToken();
        if (authIsReal(authToken)){
            var listOfAllGamesIncludingChessGames = gameDAO.getAllGames().values();
            ArrayList<GameWithNoChessGame> gamesWithNoChessGames = new ArrayList<>();
            for (var game: listOfAllGamesIncludingChessGames)  {
                gamesWithNoChessGames.add(new GameWithNoChessGame(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            return gamesWithNoChessGames;

        }
        else{
            throw new DataAccessException("Error: Auth token does not exist", 401);
        }
    }
}
