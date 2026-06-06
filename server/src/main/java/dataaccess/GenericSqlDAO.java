package dataaccess;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public abstract class GenericSqlDAO {

    public GenericSqlDAO() {
        try{
            configureDatabase();
        }
        catch(DataAccessException e){
            System.out.println("Actual error: " + e.getMessage());
            throw new DataAccessSQLException("Error: could not make the GenericSqlDAO", 500);
        }
    }

    public void clearGeneric(String nameOfTable) throws DataAccessSQLException{
        var statement = "TRUNCATE %s".formatted(nameOfTable);
        try {
            executeUpdate(statement);
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            throw new DataAccessSQLException("Error: clear failed in %s, awkward".formatted(nameOfTable),500);
        }
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param instanceof Boolean p) {
                        ps.setBoolean(i+1,p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DataAccessException("Error: executeUpdate failed", 500);
        }
    }

    protected abstract String[] getCreateStatements();


    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : getCreateStatements()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            try (var ps = conn.prepareStatement(
                    "ALTER TABLE games ADD COLUMN IF NOT EXISTS `isOver` BOOLEAN DEFAULT FALSE")) {
                ps.executeUpdate();
            } catch (SQLException ignored) {}
        } catch (SQLException ex) {
            System.out.println("SQL error: " + ex.getMessage());
            throw new DataAccessException("Error: Unable to configure database, this is inside configureDatabase", 500);
        }
    }

}