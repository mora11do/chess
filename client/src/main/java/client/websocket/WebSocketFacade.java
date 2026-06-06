package client.websocket;

import chess.ChessMove;
import client.ResponseException;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    private Integer gameID;
    private String authToken;
    public WebSocketFacade(String url, NotificationHandler notificationHandler, String authToken, Integer gameID) throws ResponseException {
        this.gameID = gameID;
        this.authToken = authToken;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            System.out.println("Connecting to: " + url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            System.out.println("Connected to server: " + this.session.isOpen());

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                        notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                    }
                    else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                        notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                    }
                    else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                        notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame() throws IOException{
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void leaveGame() throws IOException{
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void makeMove(ChessMove move) throws IOException{
        var makeMoveCommand = new MakeMoveCommand(move, authToken, gameID);
        System.out.println("Sending: " + new Gson().toJson(makeMoveCommand));
        this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
    }

    public void resign() throws IOException{
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }
}