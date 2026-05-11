package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.*;

import java.util.HashMap;


public class ListService extends GenericService {
    private final GameDAO gameDAO;

    public ListService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;

    }

    public HashMap<String, Game> list(ListRequest request) throws DataAccessException {
        String authToken = request.authToken();
        if (authIsReal(authToken)){
            return gameDAO.getAllGames();
        }
        else{
            throw new DataAccessException("Auth token does not exist");
        }
    }
}
