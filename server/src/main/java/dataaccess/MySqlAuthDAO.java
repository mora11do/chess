package dataaccess;

import models.Auth;
import models.User;

import java.sql.*;
import java.util.UUID;



public class MySqlAuthDAO extends GenericSqlDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        super();
    }

    @Override
    public Auth createAuth(User user) {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try {
            Auth returnAuth = new Auth(UUID.randomUUID().toString(), user.username());
            executeUpdate(statement, returnAuth.authToken(), user.username());
            return returnAuth;
        }
        catch (Exception e){
            throw new DataAccessSQLException("Error: createUser broke", 500);
        }
    }

    @Override
    public Auth createAuth(String authToken, String username) {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, authToken, username);
            return new Auth(authToken, username);
        }
        catch (Exception e){
            throw new DataAccessSQLException("Error: createUser broke", 500);
        }
    }

    @Override
    public Auth createAuth(String username) {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try {
            Auth returnAuth = new Auth(UUID.randomUUID().toString(), username);
            executeUpdate(statement, returnAuth.authToken(), username);
            return returnAuth;
        }
        catch (Exception e){
            throw new DataAccessSQLException("Error: createUser broke", 500);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessSQLException{
        var statement = "DELETE FROM auths WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        }
        catch (DataAccessException e){
            throw new DataAccessSQLException("Error: deleteAuth broke", 500);
        }
    }

    @Override
    public Auth getAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM users WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessSQLException("Error: getUser SQL failed", 500);
        }
        return null;
    }


    @Override
    public void clear() throws DataAccessSQLException{
        clearGeneric("auths");
    }


    private Auth readAuth(ResultSet rs) throws SQLException{
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new Auth(UUID.randomUUID().toString(), username);
    }

    protected final String[] getCreateStatements(){
        return new String[]{"""
            CREATE TABLE IF NOT EXISTS games (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
            )
            """
        };
    };

}