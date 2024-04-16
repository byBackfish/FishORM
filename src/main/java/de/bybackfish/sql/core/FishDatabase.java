package de.bybackfish.sql.core;

import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.util.JointPair;
import de.bybackfish.sql.util.ObjectMapper;
import de.bybackfish.sql.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FishDatabase {
    public final Logger logger = Logger.getLogger(FishDatabase.class.getName());
    private final DatabaseAdapter databaseAdapter;

    public FishDatabase(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public void connect(DatabaseOptions databaseOptions) throws ClassNotFoundException, SQLException {
        databaseAdapter.connect(databaseOptions);
    }

    public <T extends DatabaseModel<T>> List<T> executeQuery(String sql, Class<T> clazz, Object... params) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        ObjectMapper mapper = new ObjectMapper(clazz);

        try (PreparedStatement statement = prepareStatement(sql, params)) {
            ResultSet resultSet = statement.executeQuery();

            return mapper.map(resultSet);
        }
    }

    public void executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            statement.executeUpdate();
        }
    }

    public ResultSet nativeQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            return statement.executeQuery();
        }
    }

    private PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        int requiredParams = sql.split("\\?").length - 1;
        debug("Params used/given: {0}/{1}\n", requiredParams, params.length);

        debug("SQL: {0}\n", sql);
        debug("Parameters: {0}\n", Arrays.stream(params).map(Object::toString).collect(Collectors.joining(", ")));


        if (requiredParams > params.length) {
            throw new SQLException("""
                    Not enough parameters for query:
                    SQL: %s
                    Required: %s
                    Given: %s
                    """.formatted(sql, requiredParams, params.length));
        }

        PreparedStatement statement = databaseAdapter.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }

        debug("Prepared Statement: {0}\n", statement.toString());

        return statement;
    }

    public <T extends DatabaseModel<T>> List<T> select(SelectOptions options, Class<T> clazz, Object... params) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String tableName;

        try {
            tableName = clazz.getAnnotation(Table.class).value();
        } catch (Exception ignored) {
            tableName = clazz.getSimpleName();
        }

        String sql = "select " + options.what + " from " + tableName + " where " + options.conditions.filter;
        List<Object> finalParameters = new ArrayList<>();
        finalParameters.addAll(Arrays.asList(options.conditions.params));
        finalParameters.addAll(Arrays.asList(params));

        return executeQuery(sql, clazz, finalParameters.toArray());
    }

    // "innerJoin", that returns JointPair<T, U> where T is subclass of DatabaseModel and U is subclass of DatabaseModel
    public <T extends DatabaseModel<T>, U extends DatabaseModel<U>> List<JointPair<T, U>> join(SelectOptions options, Class<T> clazz, Class<U> clazz2, Object... params) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String firstTableName;

        try {
            firstTableName = clazz.getAnnotation(Table.class).value();
        } catch (Exception ignored) {
            firstTableName = clazz.getSimpleName();
        }

        String secondTableName;
        try {
            secondTableName = clazz2.getAnnotation(Table.class).value();
        } catch (Exception ignored) {
            secondTableName = clazz2.getSimpleName();
        }

        String finalSecondTableName = secondTableName;
        Map.Entry<Field, ForeignKey> foreignKey = ReflectionUtils.getAnnotatedFields(clazz, ForeignKey.class)
                .entrySet().stream().filter(entry -> entry.getValue().targetTable().equals(finalSecondTableName))
                .findFirst().orElseThrow(() -> new RuntimeException("No foreign key found for " + clazz.getSimpleName()));

        String foreignKeyColumnName;
        try {
            de.bybackfish.sql.annotation.Field annotationFromField = ReflectionUtils.getAnnotationFromField(foreignKey.getKey(), de.bybackfish.sql.annotation.Field.class);
            assert annotationFromField != null;
            foreignKeyColumnName = annotationFromField.value();
        } catch (Exception ignored) {
            foreignKeyColumnName = foreignKey.getKey().getName();
        }

        String sql = String.format(
                "select %s from %s inner join %s on %s.%s = %s.%s where %s",
                options.what,
                firstTableName,
                secondTableName,
                firstTableName,
                foreignKeyColumnName,
                secondTableName,
                foreignKey.getValue().targetColumn(),
                options.conditions.filter
        );

        ObjectMapper firstMapper = new ObjectMapper(clazz);
        ObjectMapper secondMapper = new ObjectMapper(clazz2);

        ResultSet resultSet = nativeQuery(sql, params);

        List<JointPair<T, U>> jointPairs = new ArrayList<>();

        List<T> firstTable = firstMapper.map(resultSet);
        resultSet.beforeFirst();
        List<U> secondTable = secondMapper.map(resultSet);

        // should be same length
        if (firstTable.size() != secondTable.size()) {
            throw new RuntimeException("Length of first and second table are not the same: " + firstTable.size() + " and " + secondTable.size());
        }

        // join together with stream
        for (int i = 0; i < firstTable.size(); i++) {
            jointPairs.add(new JointPair<>(firstTable.get(i), secondTable.get(i)));
        }

        return jointPairs;
    }

    private void log(Level level, String message, Object... params) {
        logger.log(level, message, params);
    }

    private void debug(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    public record SelectOptions(String what, FilterOptions conditions) {
        public SelectOptions(String what) {
            this(what, new FilterOptions());
        }

        public static SelectOptions All() {
            return new SelectOptions("*");
        }

        public SelectOptions withFilter(FilterOptions filter) {
            return new SelectOptions(what, filter);
        }
    }

    public record FilterOptions(String filter, Object... params) {
        public FilterOptions() {
            this("true");
        }

        public static FilterOptions ById(int id) {
            return FilterBy("id", id);
        }

        public static FilterOptions FilterBy(String key, Object value) {
            return new FilterOptions(key + " = ?", value);
        }

    }

}
