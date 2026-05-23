package ui;

import java.util.Arrays;
import java.util.Scanner;

import client.ResponseException;
import client.ServerFacade;
import models.Auth;

public class GameplayREPL {
    private final ServerFacade server;

    public GameplayREPL(ServerFacade server) throws ResponseException {
        this.server = server;
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
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
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "leave" -> "leave";
                default -> help();
            };
    }

    public String help() {
        return """
                - leave
                - help
                """;
    }
}