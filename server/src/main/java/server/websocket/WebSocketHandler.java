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
import models.Auth;
import models.Game;
import models.User;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
    public void handleMessage(WsMessageContext ctx) {
        try {
            System.out.println("handleMessage called");
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            System.out.println("Command type: " + command.getCommandType());
            Integer gameID = command.getGameID();
            Auth auth = authDAO.getAuth(command.getAuthToken());
            Session session = ctx.session;
            if (auth == null) {
                connections.broadcastToOne(session, new ErrorMessage("Error: Unauthorized"));
                return;
            }
            String username = auth.username();
            switch (command.getCommandType()) {
                case CONNECT -> connect(gameID, username, session);
                case MAKE_MOVE -> {
                    System.out.println("MAKE_MOVE CASE IN SERVER");
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    var move = moveCommand.getMove();
                    try {
                        makeMove(gameID, session, move, username);
                    } catch (InvalidMoveException e) {
                        connections.broadcastToOne(session, new ErrorMessage("Error: Invalid move"));
                    }
                }
                case LEAVE -> leave(gameID,username,session);
                case RESIGN -> resign(gameID, username, session);
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

    private void makeMove(Integer gameID, Session session, ChessMove move, String username)
            throws IOException, InvalidMoveException {
        System.out.println("GameID: " + gameID + " Move: " + move);
        var gameData = gameDAO.getGame(gameID);
        if (gameData.isOver()){
            connections.broadcastToOne(session, new ErrorMessage("Error: The game is already over, you can't move"));
            return;
        }
        String playerColor = null;
        if (username.equals(gameData.whiteUsername())){
            playerColor = "WHITE";
        }
        else if (username.equals(gameData.blackUsername())){
            playerColor = "BLACK";
        }
        else{
            connections.broadcastToOne(session, new ErrorMessage("Error: Observers can't make moves"));
            return;
        }
        var game = gameData.game();
        if (!playerColor.equals(game.getTeamTurn().toString())){
            connections.broadcastToOne(session, new ErrorMessage("Error: It's not your turn, you sly dog"));
            return;
        }
        var dummyGameJustForGameIDToBeAccurate = new Game(gameID,"FAKE","FAKE","FAKE",null, false);
        System.out.println("Piece at start: " + game.getBoard().getPiece(move.getStartPosition()));
        System.out.println("Team turn before move: " + game.getTeamTurn());
        game.makeMove(move);
        System.out.println("Team turn after move: " + game.getTeamTurn());
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
                    oldGame.blackUsername(), oldGame.gameName(), oldGame.game(), oldGame.isOver());
        }
        else{
            newGame = new Game(gameID,oldGame.whiteUsername(),
                    null, oldGame.gameName(), oldGame.game(), oldGame.isOver());
        }
        gameDAO.updateGame(oldGame, newGame);
        var message = String.format("%s has left to the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification, gameID);
    }

    private void resign(Integer gameID, String username, Session session) throws IOException{
        Game oldGame = gameDAO.getGame(gameID);
        Game newGame = new Game(gameID, oldGame.whiteUsername(),
                oldGame.blackUsername(), oldGame.gameName(), oldGame.game(), true);
        gameDAO.updateGame(oldGame, newGame);
        connections.broadcast(null, new NotificationMessage(username + " has resigned!"),gameID);
    }
}