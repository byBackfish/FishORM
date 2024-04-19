package de.bybackfish.sql.query;

public class QueryBuilder {

    public static SelectQueryBuilder select(String condition) {
        return new SelectQueryBuilder(condition);
    }

    public static InsertQueryBuilder insert(String tableName) {
        return new InsertQueryBuilder(tableName);
    }

    public static DeleteQueryBuilder delete(String tableName) {
        return new DeleteQueryBuilder(tableName);
    }

    public static UpdateQueryBuilder update(String tableName) {
        return new UpdateQueryBuilder(tableName);
    }


}
