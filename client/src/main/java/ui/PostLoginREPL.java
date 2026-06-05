package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import client.websocket.NotificationHandler;
import models.Auth;
import models.GameWithNoChessGame;

public class PostLoginREPL {
    private final ServerFacade server;
    private final Auth auth;
    private ArrayList<GameWithNoChessGame> games;
    private final Integer port;
    public PostLoginREPL(ServerFacade server, Auth auth, Integer port) throws ResponseException {
        this.server = server;
        this.auth = auth;
        this.games = server.list(auth);
        this.port = port;
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.equals("logout")) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command. Available commands:\n" + help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        server.logout(auth);
        System.out.print("You have logged out.");
        return "logout";
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            server.create(auth, params[0]);
            games = server.list(auth);
            return String.format("You created a game called %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String list() throws ResponseException {
        games = server.list(auth);
        int counterForListGames = 1;
        for (var game:games){
            System.out.println(counterForListGames + " " + game.gameName() +
                    ", White Player: " + game.whiteUsername() + ", Black Player: "
                    + game.blackUsername());
            counterForListGames++;
        }
        if (games.isEmpty()){
            System.out.println("There are no games.");
        }
        return "";
    }

    public void makeSureGameExistsBeforeJoinOrObserve(String param) throws ResponseException{
        try{
            Integer.parseInt(param);
        }
        catch(NumberFormatException e){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Please enter the game number (not the name).");
        }
        if (Integer.parseInt(param) > games.size() || Integer.parseInt(param) <= 0){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: No game with that number.");
        }
    }

    public String join(String... params) throws ResponseException {
        if (params.length >= 2) {
            makeSureGameExistsBeforeJoinOrObserve(params[0]);
            int intOfGameTheyWant = Integer.parseInt(params[0])-1;
            var gameTheyWant = games.get(intOfGameTheyWant);
            int gameID = gameTheyWant.gameID();
            String playerColor = params[1];
            server.join(auth, gameID, playerColor);
            new GameplayREPL(server, playerColor, new ChessGame(),gameID, auth.authToken(), port).run();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameNumber> <teamColor>");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length >= 1) {
            makeSureGameExistsBeforeJoinOrObserve(params[0]);
            System.out.println("You are observing a game.");
//            new GameplayREPL(server, "WHITE", new ChessGame(), port).run();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameNumber>");
    }

    public String help() {
        return """
                - logout
                - create <gameName>
                - list
                - join <gameNumber> <teamColor>
                - observe <gameNumber>;
                - help
                """;
    }
}