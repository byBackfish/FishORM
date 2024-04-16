package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import example.model.Employee;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Update {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "user", "password"));

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

    }

}
