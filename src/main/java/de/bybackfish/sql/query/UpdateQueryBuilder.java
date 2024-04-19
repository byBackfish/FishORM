package de.bybackfish.sql.query;

public class UpdateQueryBuilder extends AbstractQueryBuilder {

    boolean isSet = false;

    public UpdateQueryBuilder(String tableName) {
        super();
        sql(STR."UPDATE \{tableName}", Integer.MAX_VALUE);
    }

    public UpdateQueryBuilder set(String columnName, Object value) {
        if (isSet) {
            sql(",", Integer.MAX_VALUE - 1);
        } else {
            isSet = true;
            sql("SET", Integer.MAX_VALUE - 1);
        }
        sql(STR."\{columnName} = ?", Integer.MAX_VALUE - 1, value);
        isSet = true;
        return this;
    }
}
