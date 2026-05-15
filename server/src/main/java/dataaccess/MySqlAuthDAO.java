package dataaccess;

import models.Auth;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


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
    public void createUser(String username, String password, String email) throws DataAccessSQLException {
        var statement = "INSERT INTO users (username, hashedPassword, email) VALUES (?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            executeUpdate(statement, username, hashedPassword, email);
        }
        catch (Exception e){
            throw new DataAccessSQLException("Error: createUser broke", 500);
        }
    }

    @Override
    public User getUser(String username) throws DataAccessSQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, hashedPassword, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessSQLException("Error: getUser SQL failed", 500);
        }
        return null;
    }


    @Override
    public User getUser(User user) throws DataAccessSQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, hashedPassword, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
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
        var statement = "TRUNCATE users";
        try {
            executeUpdate(statement);
        }
        catch (DataAccessException e){
            throw new DataAccessSQLException("Error: clear failed, awkward",500);
        }
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