package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.DatabaseProvider;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;
import de.bybackfish.sql.query.QueryBuilder;
import de.bybackfish.sql.util.JointClasses;
import example.model.Department;
import example.model.Employee;
import example.model.EmployeeDepartmentLink;

import java.util.List;
import java.util.stream.Collectors;

public class Join {

    public static void main() throws FishSQLException, ClassNotFoundException {
        DatabaseProvider.setup(new PostgresAdapter());
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));

        // Join all employees with all departments
        List<JointClasses.JointPair<Employee, Department>> selectQueryBuilder =
                QueryBuilder.select("*").from("employee")
                        .join(Employee.class, Department.class).build(fishDatabase)
                        .unwrap(Employee.class, Department.class);

        for (JointClasses.JointPair<Employee, Department> pair : selectQueryBuilder) {
            System.out.printf("%s is in %s\n", pair.first(), pair.second());
        }


        // Joining one employee with one (or multiple via #linkMany) department(s)
        Employee employee = Employee.findOne(Employee.class, QueryBuilder.select("*")).get();

        // Recommended: Join one employee with one department using lazy loading (requires extra steps in the employee model)
        List<EmployeeDepartmentLink> departmentLinks = employee.department.get(); // It lazy loads the join when needed

        System.out.println(STR."\{employee.name} is in \{
                departmentLinks.stream().map(link -> link.department.get().name).collect(Collectors.joining(", "))}");

        // Alternative: Join one employee with its department using the field name.
        // This will not work when using Link Tables, like in this scenario and will only work with direct foreign keys.
        Department department = employee.linkOne(Department.class, "departmentId");

        System.out.println(STR."\{employee.name} is in \{department.name}");
        }
}
