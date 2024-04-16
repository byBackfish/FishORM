package example.model;

import de.bybackfish.sql.annotation.Field;
import de.bybackfish.sql.annotation.PrimaryKey;
import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.core.DatabaseModel;

@Table("department")
@SuppressWarnings("unused")
public class Department extends DatabaseModel {
    /**
     * @see Employee for more information
     */

    @PrimaryKey
    int id;

    @Field("department_name")
    String name;
}