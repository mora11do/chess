package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import models.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class MySqlGameDAO extends GenericSqlDAO implements GameDAO{

    public MySqlGameDAO() {
        super();
    }

    @Override
    public int createGame(String gameName) {
        try {
            var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, jsonGame) VALUES (?, ?, ?, ?)";
            String json = new Gson().toJson(new ChessGame());
            int gameID = executeUpdate(statement, null, null,
                    gameName, json);
            return gameID;
        }
        catch (Exception e) {
            throw new DataAccessSQLException("Error: createGame broke", 500);
        }

    }

    @Override
    public Game getGame(String gameName) throws DataAccessSQLException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM games WHERE gameName=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new DataAccessSQLException("Error: getGame SQL failed", 500);
        }
        return null;
    }

    @Override
    public Game getGame(int gameID) throws DataAccessSQLException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonGame FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new DataAccessSQLException("Error: getUser SQL failed", 500);
        }
        return null;
    }

    @Override
    public HashMap<String, Game> getAllGames() throws DataAccessSQLException{
        HashMap<String, Game> listOfGamesToBeReturned = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        listOfGamesToBeReturned.put(rs.getString("gameName"), readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessSQLException("Error: broke trying to get all games", 500);
        }
        return listOfGamesToBeReturned;
    }

    @Override
    public void updateGame(Game oldGame, Game newGame) throws DataAccessSQLException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, jsonGame = ? WHERE gameID = ?";
            var json = new Gson().toJson(newGame);
            executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), json, oldGame.gameID());
        } catch (Exception e) {
            throw new DataAccessSQLException("Error: updateGame failed", 500);
        }
    }

    @Override
    public void clear() {
        clearGeneric("games");
    }

    private Game readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var jsonGame = rs.getString("jsonGame");
        ChessGame chessGame = new Gson().fromJson(jsonGame, ChessGame.class);
        return new Game(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    protected final String[] getCreateStatements(){
        return new String[]{"""
        CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` VARCHAR(256) DEFAULT NULL,
          `blackUsername` VARCHAR(256) DEFAULT NULL,
          `gameName` VARCHAR(256) NOT NULL,
          `jsonGame` TEXT NOT NULL,
          PRIMARY KEY (`gameID`)
        )
        """
        };
    }

}