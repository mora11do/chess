package client;

import chess.*;
import ui.PreLoginREPL;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        try {
            new PreLoginREPL(8080).run();
        }
        catch(ResponseException e){

        }
    }
}
