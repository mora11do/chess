package models;

import chess.ChessGame;

public record GameWithNoChessGame (int gameID, String whiteUsername, String blackUsername,
                    String gameName){}
