package de.bybackfish.sql.core;

import example.DatabaseProvider;
import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.annotation.PrimaryKey;
import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.util.JointPair;
import de.bybackfish.sql.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseModel {
    public static <T extends DatabaseModel> List<T> findMany(Class<T> clazz, FishDatabase.SelectOptions options, Object... params) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        return fishDatabase.select(options, clazz, params);
    }

    public static <T extends DatabaseModel> Optional<T> findOne(Class<T> clazz, FishDatabase.SelectOptions options, Object... params) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        List<T> models = findMany(clazz, options, params);
        if (models.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(models.getFirst());
    }

    public static <T extends DatabaseModel> List<T> all(Class<T> clazz) throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return findMany(clazz, FishDatabase.SelectOptions.All());
    }

    protected static String getTableName(Class<? extends DatabaseModel> clazz) {
        try {
            return clazz.getAnnotation(Table.class).value();
        } catch (Exception ignored) {
        }
        return clazz.getSimpleName();
    }

    protected static String getFieldName(java.lang.reflect.Field field) {
        try {
            return field.getAnnotation(de.bybackfish.sql.annotation.Field.class).value();
        } catch (Exception ignored) {
        }
        return field.getName();
    }

    public <T extends DatabaseModel> List<T> linkMany(Class<T> clazz) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        String thisName = getTableName(this.getClass());
        String targetName = getTableName(clazz);

        Map.Entry<java.lang.reflect.Field, ForeignKey> target = ReflectionUtils.getAnnotatedFields(this.getClass(), ForeignKey.class).entrySet()
                .stream().filter(entry -> entry.getValue().targetTable().equals(targetName)).findFirst().orElseThrow(() -> new RuntimeException("No foreign key found"));

        String targetColumn = target.getValue().targetColumn();
        String thisField = getFieldName(target.getKey());

        JointPair<String, Collection<Object>> distinctWhereClause = getDistinctWhereClause();

        return fishDatabase.executeQuery(String.format("select %s.* from %s inner join %s on %s.%s = %s.%s where %s", targetName, thisName, targetName, thisName, thisField, targetName, targetColumn, distinctWhereClause.key), clazz, distinctWhereClause.value.toArray());
    }

    public <T extends DatabaseModel> T linkOne(Class<T> clazz) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        return linkMany(clazz).getFirst();
    }

    public void insert() throws SQLException, IllegalAccessException {
        String tableName = getTableName(this.getClass());

        StringBuilder sql = new StringBuilder(STR."insert into \{tableName} (");

        Map<String, Object> fields = new HashMap<>();
        for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = getFieldName(field);

            Object value = field.get(this);
            if (value instanceof Optional<?>) {
                value = ((Optional<?>) value).orElse(null);
            }
            if (value == null) {
                continue;
            }
            fields.put(fieldName, value);
        }

        Set<String> keys = fields.keySet();
        String fieldsString = keys.stream().map(key -> STR."\{key}, ").collect(Collectors.joining());
        fieldsString = fieldsString.substring(0, fieldsString.length() - 2);

        sql.append(fieldsString);

        sql.append(") values (");

        Collection<Object> values = fields.values();
        String valuesString = values.stream().map(_ -> "?, ").collect(Collectors.joining());
        valuesString = valuesString.substring(0, valuesString.length() - 2);

        sql.append(valuesString);

        ArrayList<Object> params = new ArrayList<>(values);
        sql.append(")");

        DatabaseProvider.getDatabase().executeUpdate(sql.toString(), params.toArray());
    }

    public void update() throws SQLException, IllegalAccessException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        String tableName = getTableName(this.getClass());

        List<java.lang.reflect.Field> fields = new ArrayList<>();
        for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(PrimaryKey.class) == null) {
                fields.add(field);
            }
        }

        List<Object> params = new ArrayList<>();

        Map<String, Object> values = new HashMap<>();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            String fieldName = getFieldName(field);
            Object value = field.get(this);
            if (value instanceof Optional<?>) {
                value = ((Optional<?>) value).orElse(null);
            }
            if (value == null) {
                continue;
            }
            values.put(fieldName, value);
            params.add(value);
        }

        JointPair<String, Collection<Object>> distinctWhereClause = getDistinctWhereClause();

        String sql = String.format(
                "update %s set %s where %s",
                tableName,
                values.keySet().stream().map(o -> String.format("%s = ?", o)).collect(Collectors.joining(", ")),
                distinctWhereClause.key
        );


        params.addAll(distinctWhereClause.value);
        fishDatabase.executeUpdate(sql, params.toArray());
    }

    protected Collection<java.lang.reflect.Field> getPrimaryKeyFields() {
        return ReflectionUtils.getAnnotatedFields(this.getClass(), PrimaryKey.class).keySet();
    }

    protected JointPair<String, Collection<Object>> getDistinctWhereClause() {
        Collection<java.lang.reflect.Field> primaryKeyFields = getPrimaryKeyFields();

        if (primaryKeyFields.isEmpty()) {
            return new JointPair<>("true", Collections.emptyList());
        }

        return new JointPair<>(
                primaryKeyFields.stream().map(field -> String.format("%s.%s = ?", getTableName(this.getClass()), getFieldName(field))).collect(Collectors.joining(" and ")),
                primaryKeyFields.stream().map(field -> {
                    try {
                        return field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        Map<String, Object> fields = new HashMap<>();
        for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = getFieldName(field);
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value instanceof Optional<?>) {
                value = ((Optional<?>) value).orElse(null);
            }
            if (value == null) {
                continue;
            }
            fields.put(fieldName, value);
        }

        return """
                %s{%s}
                """.formatted(getTableName(this.getClass()), fields.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining(", ")));
    }
}
