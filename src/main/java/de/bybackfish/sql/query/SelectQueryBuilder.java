package de.bybackfish.sql.query;

import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.core.DatabaseModel;
import de.bybackfish.sql.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

public class SelectQueryBuilder extends AbstractQueryBuilder {

    String thisTableName;

    public SelectQueryBuilder(String columns) {
        super();

        sql(STR."SELECT \{columns}", Integer.MAX_VALUE);
    }

    public SelectQueryBuilder from(String tableName) {
        this.thisTableName = tableName;
        sql(STR."FROM \{tableName}", Integer.MAX_VALUE - 1);
        return this;
    }

    public SelectQueryBuilder join(String tableName, String thisField, String thatField) {
        sql(STR."JOIN \{tableName} ON \{thisTableName}.\{thisField} = \{tableName}.\{thatField}", Integer.MAX_VALUE - 5);
        return this;
    }

    public <T extends DatabaseModel, U extends DatabaseModel> SelectQueryBuilder join(Class<T> thisTable, Class<U> otherTable) {
        String thatTableName = ReflectionUtils.getTableName(otherTable);

        Map.Entry<Field, ForeignKey> fieldForeignKeyEntry = ReflectionUtils.getAnnotatedFields(thisTable, ForeignKey.class).entrySet()
                .stream().filter(entry -> entry.getValue().targetTable().equals(thatTableName)).findFirst().orElseThrow(
                        () -> new RuntimeException(STR."No foreign key found for \{thatTableName}")
                );

        String thisFieldName = ReflectionUtils.getFieldName(fieldForeignKeyEntry.getKey());
        String thatFieldName = fieldForeignKeyEntry.getValue().targetColumn();

        return join(thatTableName, thisFieldName, thatFieldName);
    }
}
