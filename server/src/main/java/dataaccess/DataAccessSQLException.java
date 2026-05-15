package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessSQLException extends RuntimeException{
    private int statusCode;
    public DataAccessSQLException(String message) {
        super(message);
    }
    public DataAccessSQLException(String message, Throwable ex) {
        super(message, ex);
    }
    public DataAccessSQLException(String message, int statusCode){
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }
}
