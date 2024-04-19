package de.bybackfish.sql.query;

import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;

import java.sql.SQLException;
import java.util.Map;

public class InsertQueryBuilder extends AbstractQueryBuilder {

    Map<String, Object> values = new java.util.HashMap<>();

    public InsertQueryBuilder(String tableName) {
        super();
        sql(STR."INSERT INTO \{tableName}", Integer.MAX_VALUE);
    }

    public InsertQueryBuilder add(String columnName, Object value) {
        values.put(columnName, value);
        return this;
    }

    @Override
    public BuiltQuery build(FishDatabase fishDatabase) throws FishSQLException {

        // add the nodes
        // first the names of the columns
        sql(STR."(\{values.keySet().stream().map(key -> STR."\{key}").collect(java.util.stream.Collectors.joining(","))})", Integer.MAX_VALUE - 1);


        // then the values
        sql(STR."VALUES (\{values.values().stream().map(key -> "?").collect(java.util.stream.Collectors.joining(","))})", Integer.MAX_VALUE - 1, values.values().toArray());
        return super.build(fishDatabase);
    }
}
