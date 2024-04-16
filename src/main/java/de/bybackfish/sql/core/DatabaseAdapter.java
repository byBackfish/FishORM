package de.bybackfish.sql.core;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAdapter {
    void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, SQLException;

    void disconnect() throws SQLException;

    Connection getConnection() throws SQLException;
}
