package example;

import de.bybackfish.sql.core.DatabaseAdapter;
import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishSQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresAdapter implements DatabaseAdapter {
    private Connection connection;

    @Override
    public void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, FishSQLException {
        Class.forName("org.postgresql.Driver");

        String jdbcUrl = STR."jdbc:postgresql://\{databaseOptions.host()}:\{databaseOptions.port()}/\{databaseOptions.database()}";
        try {
            connection = DriverManager.getConnection(jdbcUrl, databaseOptions.username(), databaseOptions.password());
        } catch (SQLException e) {
            throw new FishSQLException(e);
        }
    }

    @Override
    public void disconnect() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
