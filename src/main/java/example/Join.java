package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.util.JointPair;
import example.model.Department;
import example.model.Employee;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class InnerJoin {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "user", "password"));


        // Join all employees with all departments
        List<JointPair<Employee, Department>> joined = fishDatabase.join(FishDatabase.SelectOptions.All(), Employee.class, Department.class);

        for (JointPair<Employee, Department> pair : joined) {
            System.out.printf("%s is in %s\n", pair.key, pair.value);
        }

        // Link a single employee with one (or multiple) departments

        // we know its present, so no need to check if the optional is filled
        Employee employee = Employee.findOne(Employee.class, FishDatabase.SelectOptions.All().withFilter(FishDatabase.FilterOptions.ById(1))).get();
        Department department = employee.linkOne(Department.class);

        System.out.println(department);
    }

}
