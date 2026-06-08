package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import chess.*;
import client.ResponseException;
import client.ServerFacade;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import models.Auth;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayREPL implements NotificationHandler {
    private final ServerFacade server;
    private final String playerColor;
    private ChessGame game;
    private final Integer port;
    private WebSocketFacade ws;
    private final boolean isObserver;

    public GameplayREPL(ServerFacade server, String playerColor, ChessGame game,
                        Integer gameID, String authToken, Integer port, boolean isObserver) throws ResponseException {
        this.server = server;
        this.playerColor = playerColor;
        this.game = game;
        this.port = port;
        this.ws = new WebSocketFacade("http://localhost:"+ port.toString(), this, authToken,gameID);
        this.isObserver = isObserver;
        try{
            ws.connectToGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            String line = scanner.nextLine();
            if (!line.equals("leave")) {
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


    public String eval(String input) throws IOException, ResponseException{
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "resign" -> resign();
                case "move" -> move(params);
                case "help" -> help();
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "highlight" -> displayLegalMoves(params);

                default -> "Unknown command. Available commands:\n" + help();
            };
        } catch (ResponseException ex) {
        return ex.getMessage();
    }
    }

    public String displayLegalMoves(String... params) throws ResponseException{
        if (params.length==1) {
            try {
                ChessBoard board = game.getBoard();
                ChessPosition position = easyChessPosition(params[0]);
                MovementCalculator calculator = new MovementCalculator(board, position);
                ChessPiece.PieceType pieceType = board.getPiece(position).getPieceType();
                ArrayList<ChessMove> legalMoves;
                switch (pieceType) {
                    case PAWN -> legalMoves = calculator.calculatePawn();
                    case KING -> legalMoves = calculator.calculateKing();
                    case ROOK -> legalMoves = calculator.calculateRook();
                    case QUEEN -> legalMoves = calculator.calculateQueen();
                    case KNIGHT -> legalMoves = calculator.calculateKnight();
                    case BISHOP -> legalMoves = calculator.calculateBishop();
                    default -> legalMoves = new ArrayList<ChessMove>();
                }
                drawBoard(legalMoves);
                return "";
            }catch(IndexOutOfBoundsException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid input. Expected: highlight <piece position>");
            }
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid input. Expected: highlight <piece position>");
        }
    }

    public String help() {
        return """
                - move <start position> <end position> <optional promotion piece for pawns>
                - leave
                - resign
                - redraw
                - highlight <piece position>
                - help
                """;
    }

    public String leave() throws IOException{
        ws.leaveGame();
        return "leave";
    }

    public ChessPosition easyChessPosition(String letterNumber){
        int col = Character.getNumericValue(letterNumber.charAt(0)) - Character.getNumericValue('a') + 1;
        int row = Character.getNumericValue(letterNumber.charAt(1));
        return new ChessPosition(row,col);
    }

    public ChessMove easyChessMove(String letterNumberStart, String letterNumberEnd, String promoPieceType)
            throws InvalidMoveException{
        ChessPosition startPosition = easyChessPosition(letterNumberStart);
        ChessPosition endPosition = easyChessPosition(letterNumberEnd);
        ChessPiece.PieceType promo;
        switch (promoPieceType){
            case "bishop" -> promo = ChessPiece.PieceType.BISHOP;
            case "knight" -> promo = ChessPiece.PieceType.KNIGHT;
            case "rook" -> promo = ChessPiece.PieceType.ROOK;
            case "queen" -> promo = ChessPiece.PieceType.QUEEN;
            default -> throw new InvalidMoveException("Error: Invalid promotion piece");
        }
        return new ChessMove(startPosition, endPosition, promo);
    }

    public String move(String... params) throws IOException, ResponseException{
        if (isObserver){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: You're a nerd and an observer, you can't move");
        }
        if (params.length == 2){
            try {
                ws.makeMove(new ChessMove(easyChessPosition(params[0]), easyChessPosition(params[1]), null));
            } catch (StringIndexOutOfBoundsException e){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid input (example: move a2 a3)");
            }
        }
        else if (params.length == 3){
            try{
                ws.makeMove(easyChessMove(params[0], params[1], params[2]));
        } catch (InvalidMoveException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid promotion piece");
            }catch(Exception e){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid input");
            }
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <startPosition> <endPosition> <optionalPromotionPiece>");
        }
        return "";
    }

    public String resign() throws IOException, ResponseException {
        if (isObserver){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: You're can't resign if you're an observer");
        }
        ws.resign();
        return "Resigned from the game";
    }

    public void print(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType){
        String stringPieceType;
        String stringTeamColor;
        if (teamColor == ChessGame.TeamColor.WHITE){
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        else{
            stringTeamColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
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

    public void setHighlightBGColor(){
        System.out.print(EscapeSequences.SET_BG_COLOR_RED);
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

    public String redraw(){
        drawBoard();
        return"";
    }

    public void drawBoard(){
        drawBoard(null);
    }

    public void drawBoard(ArrayList<ChessMove> placesToHighlight){
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
                        piece = game.getBoard().getPiece(new ChessPosition(9-row, col));
                    }
                    else{
                        piece = game.getBoard().getPiece(new ChessPosition(row, 9-col));
                    }
                    setWhiteBGColor(row, col);
                    if (!(placesToHighlight==null)){
                        for (var move:placesToHighlight) {
                            ChessPosition legalMove = move.getEndPosition();
                            if (playerColor.equals("WHITE") || playerColor.equals("white")) {
                                if (legalMove.getColumn() == col && legalMove.getRow() == 9-row) {
                                    setHighlightBGColor();
                                }
                            }
                            else{
                                if (legalMove.getColumn() == 9-col && legalMove.getRow() == row) {
                                    setHighlightBGColor();
                                }
                            }
                        }
                    }
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

    public void notify(ServerMessage message){
        switch (message.getServerMessageType()){
            case LOAD_GAME -> loadGame(message);
            case NOTIFICATION -> notification(message);
            case ERROR -> error(message);
        }
    }

    public void loadGame(ServerMessage message){
        LoadGameMessage loadGameMessage = (LoadGameMessage) message;
        this.game = loadGameMessage.getGame();
        drawBoard();
    }

    public void notification(ServerMessage message){
        NotificationMessage notificationMessage = (NotificationMessage) message;
        System.out.println(notificationMessage.getMessage());

    }

    public void error(ServerMessage message){
        ErrorMessage errorMessage = (ErrorMessage) message;
        System.out.println(errorMessage.getErrorMessage());
    }
}