package de.bybackfish.sql.query;

public class DeleteQueryBuilder extends AbstractQueryBuilder {
    public DeleteQueryBuilder(String tableName) {
        super();
        sql(STR."DELETE FROM \{tableName}", Integer.MAX_VALUE);
    }
}
