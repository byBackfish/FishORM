package example;

import de.bybackfish.sql.core.DatabaseAdapter;
import de.bybackfish.sql.core.DatabaseOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresAdapter implements DatabaseAdapter {
    private Connection connection;

    @Override
    public void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        String jdbcUrl = "jdbc:postgresql://" + databaseOptions.host() + ":" + databaseOptions.port() + "/" + databaseOptions.database();
        connection = DriverManager.getConnection(jdbcUrl, databaseOptions.username(), databaseOptions.password());
    }

    @Override
    public void disconnect() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
