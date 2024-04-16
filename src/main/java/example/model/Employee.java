package example.model;

import de.bybackfish.sql.annotation.Field;
import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.annotation.PrimaryKey;
import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.core.DatabaseModel;

// Create a model that represents an "Employee" from the "employee" table

@Table("employee")
@SuppressWarnings("unused")
public class Employee extends DatabaseModel {
    public String name;

    // @Field is used to map the "department_id" column to the "departmentId" field
    @Field("department_id")
    // @ForeignKey tells the ORM to join those fields together if wanted
    @ForeignKey(targetTable = "department", targetColumn = "id")
    public int departmentId;

    // Primary Key, used to distinct between rows.
    // Every Model should have one if you want to update rows.
    @PrimaryKey
    int id;
}
