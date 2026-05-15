package dataaccess;

import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlUserDAO extends GenericSqlDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        super();
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

    private User readUser(ResultSet rs) throws SQLException{
        var username = rs.getString("username");
        var hashedPassword = rs.getString("hashedPassword");
        var email = rs.getString("email");
        return new User(username, hashedPassword, email);
    }

    protected final String[] getCreateStatements(){
            return new String[]{"""
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `hashedPassword` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL
              PRIMARY KEY (`username`),
            )
            """
            };
    };

}