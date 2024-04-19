package de.bybackfish.sql.core;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAdapter {
    void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, FishSQLException;

    void disconnect() throws FishSQLException;

    Connection getConnection() throws FishSQLException;
}
