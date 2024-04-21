package example.model;

import de.bybackfish.sql.annotation.*;
import de.bybackfish.sql.core.DatabaseModel;
import de.bybackfish.sql.util.Lazy;

// Create a model that represents an "Employee" from the "employee" table

@Table("employee")
@SuppressWarnings("unused")
public class Employee extends DatabaseModel {
    public String name;

    // @Field is used to map the "department_id" column to the "departmentId" field
    @Field("department_id")
    @ForeignKey(targetTable = "department", targetColumn = "id")
    // @ForeignKey tells the ORM to join those fields together if wanted
    public int departmentId;

    // Primary Key, used to distinct between rows.
    // Every Model should have one if you want to update rows.
    @PrimaryKey
    int id;

    // Lazy is used to automatically load the relation to the target table when needed.
    // @LazyLoaded takes in the field of this class that has the @ForeignKey annotation.
    @LazyLoaded("departmentId")
    public Lazy<Department> department;
}
