package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.util.JointPair;
import example.model.Department;
import example.model.Employee;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Update {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "user", "password"));

        try {
            FishDatabase.SelectOptions selectOptions = new FishDatabase.SelectOptions(
                    "*",
                    new FishDatabase.FilterOptions(
                            "id = 5"
                    )
            );

            Employee.findOne(Employee.class, selectOptions).ifPresent(employee -> {
                employee.name = "John Doe";
                try {
                    employee.update();
                } catch (SQLException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
