package models;

import chess.ChessGame;

public record Game (int gameID, String whiteUsername, String blackUsername,
                    String gameName, ChessGame game, boolean isOver) {

    public Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(gameID, whiteUsername, blackUsername, gameName, game, false);
    }
}