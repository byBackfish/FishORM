package de.bybackfish.sql.query;

public class Helper {

    public static WhereQueryBuilder where() {
        return new WhereQueryBuilder();
    }

    public static WhereQueryBuilder where(String where, Object... params) {
        return new WhereQueryBuilder().and(where, params);
    }

}
