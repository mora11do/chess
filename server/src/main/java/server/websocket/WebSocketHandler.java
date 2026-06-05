package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import models.Game;
import models.User;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws InvalidMoveException{
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            Integer gameID = command.getGameID();
            String username = authDAO.getAuth(command.getAuthToken()).username();
            Session session = ctx.session;
            switch (command.getCommandType()) {
                case CONNECT -> connect(gameID, username, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    var move = moveCommand.getMove();
                    makeMove(gameID, session, move);
                }
                case LEAVE -> leave(gameID,username,session);
//                case RESIGN ->
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    private void connect(Integer gameID, String username, Session session) throws IOException{
        connections.add(gameID, session);
        Game gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        connections.broadcastToOne(session, loadGameMessage);
        String colorOrObserver;
        if (gameData.whiteUsername().equals(username)){
            colorOrObserver = "white";
        }
        else if (gameData.blackUsername().equals(username)){
            colorOrObserver = "black";
        }
        else{
            colorOrObserver = "an observer";
        }
        var message = String.format("%s has connected to the game as %s", username, colorOrObserver);
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification, gameID);
    }

    private void makeMove(Integer gameID, Session session, ChessMove move)
            throws IOException, InvalidMoveException {
        var gameData = gameDAO.getGame(gameID);
        var game = gameData.game();
        var dummyGameJustForGameIDToBeAccurate = new Game(gameID,"FAKE","FAKE","FAKE",null);
        game.makeMove(move);
        gameDAO.updateGame(dummyGameJustForGameIDToBeAccurate, gameData);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        connections.broadcast(null, loadGameMessage,gameID);
        var message = String.format("%s has moved to %s", move.getStartPosition().toString(), move.getEndPosition().toString());
        NotificationMessage moveNotification = new NotificationMessage(message);
        connections.broadcast(session,moveNotification, gameID);
        String checkMessage = "NONE";
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            checkMessage = "BLACK WINS (white is in checkmate)";
        }
        else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            checkMessage = "WHITE WINS (black is in checkmate)";
        }
        else if (game.isInCheck(ChessGame.TeamColor.WHITE)){
            checkMessage = "White is in check!";
        }
        else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            checkMessage = "Black is in check!";
        }
        else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            checkMessage = "White is in stalemate!";
        }
        else if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            checkMessage = "Black is in stalemate!";
        }
        if (!checkMessage.equals("NONE")){
            NotificationMessage notificationMessage = new NotificationMessage(checkMessage);
            connections.broadcast(null, notificationMessage, gameID);
        }
    }

    private void leave(Integer gameID, String username, Session session) throws IOException{
        connections.remove(gameID, session);
        var oldGame = gameDAO.getGame(gameID);
        Game newGame;
        if (username.equals(gameDAO.getGame(gameID).whiteUsername())) {
            newGame = new Game(gameID,null,
                    oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }
        else{
            newGame = new Game(gameID,oldGame.whiteUsername(),
                    null, oldGame.gameName(), oldGame.game());
        }
        gameDAO.updateGame(oldGame, newGame);
        var message = String.format("%s has left to the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification, gameID);
    }

//    private void resign(Integer gameID, String username, Session session){
//        connections
//    }
}