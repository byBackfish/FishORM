package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.util.JointPair;
import example.model.Department;
import example.model.Employee;

import java.sql.SQLException;
import java.util.List;

public class InnerJoin {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "user", "password"));

        try {
            List<JointPair<Employee, Department>> joined = fishDatabase.join(FishDatabase.SelectOptions.All(), Employee.class, Department.class);

            for (JointPair<Employee, Department> pair : joined) {
                System.out.printf("%s is in %s\n", pair.key, pair.value);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
