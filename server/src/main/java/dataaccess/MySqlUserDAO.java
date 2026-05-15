package dataaccess;

import com.google.gson.Gson;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public Pet addPet(Pet pet) throws ResponseException {
        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(pet);
        int id = executeUpdate(statement, pet.name(), pet.type(), json);
        return new Pet(id, pet.name(), pet.type());
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        executeUpdate(statement, username, hashedPassword, email);
    }

    public Pet getPet(int id) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM pet WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readPet(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public User getUser(User user) {
        return users.get(user.username());
    }

    public PetList listPets() throws ResponseException {
        var result = new PetList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM pet";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void deletePet(Integer id) throws ResponseException {
        var statement = "DELETE FROM pet WHERE id=?";
        executeUpdate(statement, id);
    }

    public void deleteAllPets() throws ResponseException {
        var statement = "TRUNCATE pet";
        executeUpdate(statement);
    }

    private Pet readPet(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("json");
        Pet pet = new Gson().fromJson(json, Pet.class);
        return pet.setId(id);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String databaseName = DatabaseManager.getDatabaseName();

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL
              PRIMARY KEY (`username`),
            )
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database, this is inside configureDatabase", 500);
        }
    }


        @Override
        public void clear() {
            users.clear();
        }
    }

}