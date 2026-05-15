import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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