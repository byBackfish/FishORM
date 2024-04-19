package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.query.QueryBuilder;
import de.bybackfish.sql.query.SelectQueryBuilder;
import de.bybackfish.sql.util.JointClasses;
import example.model.Department;
import example.model.Employee;
import org.postgresql.core.Query;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Join {

    public static void main() throws SQLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));


        // Join all employees with all departments

        List<JointClasses.JointPair<Employee, Department>> selectQueryBuilder = QueryBuilder.select("*").from("employee").join(Employee.class, Department.class).build(fishDatabase).unwrap(Employee.class, Department.class);

        for (JointClasses.JointPair<Employee, Department> pair : selectQueryBuilder) {
            System.out.printf("%s is in %s\n", pair.first(), pair.second());
        }

        // Join all employees with all departments
        Employee employee = Employee.findOne(Employee.class, QueryBuilder.select("*")).get();
        Department department = employee.linkOne(Department.class);

        System.out.println(department);
    }

}
