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
        drawBoard();
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            String line = scanner.nextLine();
            if (!line.equals("leave")) {
                drawBoard();

                try {
                    result = eval(line);
                    System.out.println(result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            else{
                result = "leave";
            }
        }
        System.out.println("You left the game.");
    }


    public String eval(String input) {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "leave" -> leave();
                default -> "Unknown command. Available commands:\n" + help();
            };
    }

    public String help() {
        return """
                - leave (WARNING: If you leave, you can't rejoin this game!)
                - help
                """;
    }

    public String leave(){

        return "leave";
    }

    public void print(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType){
        String stringPieceType;
        String stringTeamColor;
        if (teamColor == ChessGame.TeamColor.WHITE){
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
        }
        else{
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        }

        if (pieceType == ChessPiece.PieceType.PAWN){
            stringPieceType = " P ";
        }
        else if (pieceType == ChessPiece.PieceType.BISHOP){
            stringPieceType = " B ";
        }
        else if (pieceType == ChessPiece.PieceType.KNIGHT){
            stringPieceType = " N ";
        }
        else if (pieceType == ChessPiece.PieceType.QUEEN){
            stringPieceType = " Q ";
        }
        else if (pieceType == ChessPiece.PieceType.KING){
            stringPieceType = " K ";
        }
        else if (pieceType == ChessPiece.PieceType.ROOK){
            stringPieceType = " R ";
        }
        else{
            System.out.println("You entered an invalid pieceType for drawing the board");
            return;
        }
        System.out.print(stringTeamColor + stringPieceType);
    }

    public void setWhiteBGColor(int col, int row){
        if ((row+col)%2 == 1){
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        }
        else{
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        }
    }

    public void printWhiteLetterRow(){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("    a  b  c  d  e  f  g  h    ");
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK);
    }

    public void printBlackLetterRow(){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("    h  g  f  e  d  c  b  a    ");
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK);
    }

    public void printWhiteNumberSquare(int row){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" "+(9-row)+" ");
    }

    public void printBlackNumberSquare(int row){
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" "+(row)+" ");
    }

    public void drawBoard(){
        if (playerColor.equals("WHITE") || playerColor.equals("white")) {
            printWhiteLetterRow();
        }
        else{
            printBlackLetterRow();
        }
            for (int row = 1; row<9; row++){
                if (playerColor.equals("WHITE") || playerColor.equals("white")) {
                    printWhiteNumberSquare(row);
                }
                else{
                    printBlackNumberSquare(row);
                }
                for (int col = 1; col<9; col++) {
                    ChessPiece piece;
                    if (playerColor.equals("WHITE") || playerColor.equals("white")) {
                        piece = game.getBoard().getPiece(new ChessPosition(row, col));
                    }
                    else{
                        piece = game.getBoard().getPiece(new ChessPosition(9-row, 9-col));
                    }
                    setWhiteBGColor(row, col);
                    if (piece == null){
                        System.out.print(EscapeSequences.EMPTY);
                    }
                    else{
                        print(piece.getTeamColor(), piece.getPieceType());
                    }
                }
                if (playerColor.equals("WHITE") || playerColor.equals("white")) {
                    printWhiteNumberSquare(row);
                }
                else{
                    printBlackNumberSquare(row);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                System.out.println();
            }
        if (playerColor.equals("WHITE") || playerColor.equals("white")) {
            printWhiteLetterRow();
        }
        else{
            printBlackLetterRow();
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
}