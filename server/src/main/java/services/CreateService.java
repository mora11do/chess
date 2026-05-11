package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.*;

import java.util.HashMap;


public class CreateService extends GenericService {
    private final GameDAO gameDAO;

    public CreateService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;

    }

    public String create(CreateRequest request) throws DataAccessException {
        String authToken = request.authToken();
        String gameName = request.gameName();
        if (authIsReal(authToken)){
            if (gameDAO.getGame(gameName) == null) {
                return gameDAO.createGame(gameName);
            }
            else{
                throw new DataAccessException("Game name is already taken");
            }
        }
        else{
            throw new DataAccessException("Auth token does not exist");
        }
    }
}
