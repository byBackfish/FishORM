package de.bybackfish.sql.core;

import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.query.AbstractQueryBuilder;
import de.bybackfish.sql.query.SelectQueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FishDatabase {
    public final Logger logger = Logger.getLogger(FishDatabase.class.getName());
    private final DatabaseAdapter databaseAdapter;

    public FishDatabase(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, FishSQLException {
        databaseAdapter.connect(databaseOptions);
    }

    public <T extends DatabaseModel> List<T> executeQuery(AbstractQueryBuilder queryBuilder, Class<T> clazzs) throws FishSQLException {
        return queryBuilder.build(this).unwrap(clazzs);
    }

    public void executeUpdate(AbstractQueryBuilder queryBuilder) throws FishSQLException {
        queryBuilder.build(this).executeUpdate();
    }

    public void nativeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            statement.executeUpdate();
        }
    }

    public ResultSet nativeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement statement = prepareStatement(sql, params);
        return statement.executeQuery();
    }

    public PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        int requiredParams = sql.split("\\?").length - 1;
        debug("Params used/given: {0}/{1}\n", requiredParams, params.length);

        debug("SQL: {0}\n", sql);
        debug("Parameters: {0}\n", Arrays.stream(params).map(Object::toString).collect(Collectors.joining(", ")));


        if (requiredParams != params.length) {
            throw new SQLException("""
                    Not enough parameters for query:
                    SQL: %s
                    Required: %s
                    Given: %s
                    """.formatted(sql, requiredParams, params.length));
        }

        Connection connection = databaseAdapter.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(STR."\{sql}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        debug("Prepared Statement: {0}\n", preparedStatement.toString());

        return preparedStatement;
    }

    public <T extends DatabaseModel> List<T> select(SelectQueryBuilder selectQueryBuilder, Class<T> clazz, Object... params) throws FishSQLException {
        String tableName;

        try {
            tableName = clazz.getAnnotation(Table.class).value();
        } catch (Exception ignored) {
            tableName = clazz.getSimpleName();
        }

        selectQueryBuilder.from(tableName);

        return executeQuery(selectQueryBuilder, clazz);
    }

    public <T extends DatabaseModel> T selectOne(SelectQueryBuilder selectQueryBuilder, Class<T> clazz, Object... params) throws FishSQLException {
        List<T> models = select(selectQueryBuilder, clazz, params);
        selectQueryBuilder.limit(1);
        if (models.isEmpty()) {
            return null;
        }
        return models.getFirst();
    }

    private void log(Level level, String message, Object... params) {
        logger.log(level, message, params);
    }

    private void debug(String message, Object... params) {
        log(Level.INFO, message, params);
    }

}
