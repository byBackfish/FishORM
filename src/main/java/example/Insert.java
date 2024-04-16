package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.FishDatabase;
import example.model.Employee;

import java.sql.SQLException;

public class Insert {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException {
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();
        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "user", "password"));

        Employee employee = new Employee();
        employee.name = "Maik";
        employee.departmentId = 1;

        employee.insert();
    }

}
