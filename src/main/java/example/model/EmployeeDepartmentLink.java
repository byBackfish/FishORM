package example.model;

import de.bybackfish.sql.annotation.Field;
import de.bybackfish.sql.annotation.ForeignKey;
import de.bybackfish.sql.annotation.LazyLoaded;
import de.bybackfish.sql.annotation.Table;
import de.bybackfish.sql.core.DatabaseModel;
import de.bybackfish.sql.util.Lazy;

@Table("employee_department_link")
public class EmployeeDepartmentLink extends DatabaseModel {
    @Field("employee_id")
    @ForeignKey(targetTable = "employee", targetColumn = "id")
    int employeeId;

    @Field("department_id")
    @ForeignKey(targetTable = "department", targetColumn = "id")
    int departmentId;

    @LazyLoaded(value = "employeeId")
    public Lazy<Employee> employee;

    @LazyLoaded(value = "departmentId")
    public Lazy<Department> department;
}
