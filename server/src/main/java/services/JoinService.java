package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.*;

import java.util.HashMap;


public class JoinService extends GenericService {
    private final GameDAO gameDAO;

    public JoinService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;

    }

    public void join(JoinRequest request) throws DataAccessException {
        String authToken = request.authToken();
        String playerColor = request.playerColor();
        int gameID = request.gameID();
        if (playerColor == null){
            throw new DataAccessException("Error: Please include a player color", 400);
        }
        if (authIsReal(authToken)){
            var gameWantToJoin = gameDAO.getGame(gameID);
            if (gameWantToJoin != null){
                if (playerColor.equals("WHITE") || playerColor.equals("white")) {
                    if (gameWantToJoin.whiteUsername() == null){
                        gameDAO.updateGame(gameWantToJoin, new Game(gameWantToJoin.gameID(), authDAO.getAuth(authToken).username(),
                                gameWantToJoin.blackUsername(), gameWantToJoin.gameName(), gameWantToJoin.game()));
                    }
                    else{
                        throw new DataAccessException("Error: White is already taken", 403);
                    }
                }
                else if (playerColor.equals("BLACK") || playerColor.equals("black")) {
                    if (gameWantToJoin.blackUsername() == null) {
                        gameDAO.updateGame(gameWantToJoin, new Game(gameWantToJoin.gameID(), gameWantToJoin.whiteUsername(),
                                authDAO.getAuth(authToken).username(), gameWantToJoin.gameName(), gameWantToJoin.game()));
                    } else {
                        throw new DataAccessException("Error: Black is already taken", 403);
                    }
                }
                else{
                    throw new DataAccessException("Error: Please enter a valid color", 400);
                }
            }
            else{
                throw new DataAccessException("Error: Game does not exist", 400);
            }
        }
        else{
            throw new DataAccessException("Error: Auth token does not exist", 401);
        }
    }
}
