package de.bybackfish.sql.core;

import java.sql.SQLException;

public class FishSQLException extends SQLException {
    public FishSQLException(String message) {
        super(message);
    }

    public FishSQLException(String message, SQLException cause) {
        super(message, cause);
    }


    public FishSQLException(Exception e) {
        super(e);
    }
}
