package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.DatabaseProvider;
import de.bybackfish.sql.core.FishDatabase;
import example.model.Employee;

import java.sql.SQLException;

public class Insert {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException {
        DatabaseProvider.setup(new PostgresAdapter());
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));

        Employee employee = new Employee();
        employee.name = "Test Employee";
        employee.departmentId = 1;


        System.out.println("Pre ID: " + employee.id);
        employee.insertAndUpdateModel();
        System.out.println("Post ID: " + employee.id);

        // Or:
        // Employee employee1 = employee.insertAndReturnModel(Employee.class);
        // System.out.println("Post ID: " + employee1.id);
    }

}
