package ui;

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ResponseException;
import client.ServerFacade;
import models.Auth;

public class GameplayREPL {
    private final ServerFacade server;
    private final String playerColor;
    private ChessGame game;

    public GameplayREPL(ServerFacade server, String playerColor, ChessGame game) throws ResponseException {
        this.server = server;
        this.playerColor = playerColor;
        this.game = game;
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            drawBoard();
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

    public void print(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType){
        String stringPieceType;
        String stringTeamColor;
        if (teamColor == ChessGame.TeamColor.BLACK){
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
        }
        else{
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        }

        if (pieceType == ChessPiece.PieceType.PAWN){
            stringPieceType = "P";
        }
        else if (pieceType == ChessPiece.PieceType.BISHOP){
            stringPieceType = "B";
        }
        else if (pieceType == ChessPiece.PieceType.KNIGHT){
            stringPieceType = "N";
        }
        else if (pieceType == ChessPiece.PieceType.QUEEN){
            stringPieceType = "Q";
        }
        else if (pieceType == ChessPiece.PieceType.KING){
            stringPieceType = "K";
        }
        else if (pieceType == ChessPiece.PieceType.ROOK){
            stringPieceType = "R";
        }
        else{
            System.out.println("You entered an invalid pieceType for drawing the board");
            return;
        }
        System.out.print(stringTeamColor + stringPieceType);
    }

    public void setBGColor(int col, int row){
        if ((row+col)%2 == 0){
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        }
        else{
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        }
    }

    public void printWhiteLetterRow(){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("    a  b  c  d  e  f  g  h    ");
    }

    public void printWhiteNumberSquare(int row){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" "+(9-row)+" ");
    }

    public void drawBoard(){
        if (playerColor.equals("WHITE")){
            printWhiteLetterRow();
            for (int row = 1; row<9; row++){
                printWhiteNumberSquare(row);
                for (int col = 1; col<9; col++) {
                    ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
                    setBGColor(row, col);
                    if (piece == null){
                        System.out.print(EscapeSequences.EMPTY);
                    }
                    else{
                        print(piece.getTeamColor(), piece.getPieceType());
                    }
                }
                printWhiteNumberSquare(row);
                System.out.println();
            }
            printWhiteLetterRow();;
        }
        else{

        }
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
}