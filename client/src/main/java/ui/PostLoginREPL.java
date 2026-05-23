package ui;

import java.util.Arrays;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;
import models.Auth;

public class PostLoginREPL {
    private final ServerFacade server;
    private final Auth auth;

    public PostLoginREPL(ServerFacade server, Auth auth) throws ResponseException {
        this.server = server;
        this.auth = auth;
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(result);
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
//                case "observe" -> observe(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        server.logout(auth);
        return "You logged out.";
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            server.create(auth, params[0]);
            return String.format("You created a game called %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String list() throws ResponseException {
        var games = server.list(auth);
        int counterForListGames = 1;
        for (var game:games){
            System.out.print(counterForListGames);
            counterForListGames++;
            System.out.println(" " + game.toString());
        }
        return "";
    }

    public String join(String... params) throws ResponseException {
        if (params.length >= 2) {
            server.join(auth, Integer.parseInt(params[0]), params[1]);
            new GameplayREPL(server);
            return "You joined a game successfully.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID> <teamColor>");
    }

    public String help() {
        return """
                - logout
                - create <gameName>
                - list
                - join
                - observe -> observe(params);
                - help
                """;
    }
}