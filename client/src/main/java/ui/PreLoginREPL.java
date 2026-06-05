package ui;

import java.util.Arrays;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;
import models.Auth;
import models.User;

public class PreLoginREPL {
    private final ServerFacade server;
    private Auth auth = null;
    private final Integer port;

    public PreLoginREPL(int port) throws ResponseException {
        server = new ServerFacade(port);
        this.port = port;
    }

    public void run() {
        System.out.println("Welcome to Chess. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
                System.out.println();
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
                case "login" -> login(params);
                case "register" -> register(params);
                case "help" -> help();
                case "clearhack" -> clear();
                case "quit" -> "quit";
                default -> "Unknown command. Available commands:\n" + help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            this.auth = server.login(new User(params[0],params[1],"fakeEmail"));
            System.out.println(String.format("You signed in as %s.", params[0]));
            new PostLoginREPL(server, auth, port).run();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname> <yourpassword>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            this.auth = server.register(new User(params[0],params[1],params[2]));
            System.out.println(String.format("You are now registered and signed in as %s.", params[0]));
            new PostLoginREPL(server, auth, port).run();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourName> <yourPassword> <yourEmail>");
    }

    public String clear() throws ResponseException {
        server.clear();
        return "DATABASE HAS BEEN CLEARED";
    }

    public String help() {
        return """
                - help
                - register <yourName> <yourPassword> <yourEmail>
                - login <yourName> <yourPassword>
                - quit
                """;
    }
}