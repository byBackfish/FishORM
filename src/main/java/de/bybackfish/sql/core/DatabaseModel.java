package de.bybackfish.sql.core;

import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.annotation.PrimaryKey;
import de.bybackfish.sql.query.*;
import de.bybackfish.sql.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static de.bybackfish.sql.util.ReflectionUtils.*;

public class DatabaseModel {
    public static <T extends DatabaseModel> List<T> findMany(Class<T> clazz, SelectQueryBuilder queryBuilder) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        return fishDatabase.select(queryBuilder, clazz);
    }

    public static <T extends DatabaseModel> Optional<T> findOne(Class<T> clazz, SelectQueryBuilder queryBuilder) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        queryBuilder.limit(1);
        List<T> models = findMany(clazz, queryBuilder);
        if (models.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(models.getFirst());
    }

    public static <T extends DatabaseModel> List<T> all(Class<T> clazz) throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return findMany(clazz, QueryBuilder.select("*"));
    }

    public <T extends DatabaseModel> List<T> linkMany(Class<T> clazz) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        String thisName = getTableName(this.getClass());
        String targetName = getTableName(clazz);

        Map.Entry<java.lang.reflect.Field, ForeignKey> target = ReflectionUtils.getAnnotatedFields(this.getClass(), ForeignKey.class).entrySet()
                .stream().filter(entry -> entry.getValue().targetTable().equals(targetName)).findFirst().orElseThrow(() -> new RuntimeException("No foreign key found"));

        String targetColumn = target.getValue().targetColumn();
        String thisField = getFieldName(target.getKey());

        WhereQueryBuilder whereQueryBuilder = getDistinctWhereClause();


        SelectQueryBuilder selectQueryBuilder = QueryBuilder.select("*").from(thisName).join(targetName, thisField, targetColumn);

        if (whereQueryBuilder != null)
            selectQueryBuilder.where(whereQueryBuilder);

        return fishDatabase.executeQuery(selectQueryBuilder, clazz);
    }

    public <T extends DatabaseModel> T linkOne(Class<T> clazz) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        return linkMany(clazz).getFirst();
    }

    public void insert() throws SQLException, IllegalAccessException {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(getTableName(this.getClass()));

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
            insertQueryBuilder.add(fieldName, value);
        }

        DatabaseProvider.getDatabase().executeUpdate(insertQueryBuilder);
    }

    public void update() throws SQLException, IllegalAccessException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        String tableName = getTableName(this.getClass());

        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(
                tableName
        );

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
            updateQueryBuilder.set(fieldName, value);
        }

        WhereQueryBuilder whereQueryBuilder = getDistinctWhereClause();

        if (whereQueryBuilder != null) {
            updateQueryBuilder.where(whereQueryBuilder);
        }

        fishDatabase.executeUpdate(updateQueryBuilder);
    }

    protected Collection<java.lang.reflect.Field> getPrimaryKeyFields() {
        return ReflectionUtils.getAnnotatedFields(this.getClass(), PrimaryKey.class).keySet();
    }

    protected WhereQueryBuilder getDistinctWhereClause() throws IllegalAccessException {
        Collection<java.lang.reflect.Field> primaryKeyFields = getPrimaryKeyFields();

        if (primaryKeyFields.isEmpty()) {
            return new WhereQueryBuilder().and("true");
        }

        WhereQueryBuilder whereQueryBuilder = new WhereQueryBuilder();

        for (java.lang.reflect.Field field : primaryKeyFields) {
            whereQueryBuilder.and(STR."\{getTableName(this.getClass())}.\{getFieldName(field)} = ?", field.get(this));
        }

        return whereQueryBuilder;
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
