package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.DatabaseProvider;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;
import example.model.Employee;

public class Transaction {

    public static void main() throws FishSQLException, ClassNotFoundException {
        DatabaseProvider.setup(new PostgresAdapter());
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));

        fishDatabase.openTransaction(() -> {
                    for (int i = 0; i < 10; i++) {
                        Employee employee = new Employee();
                        employee.name = STR."Employee \{i}";
                        employee.insert();
                    }
                    // Return false if the transaction should be rolled back (e.g. an error occurred)
                    return true;

                }, (error) -> {
                    /* On Rollback / Exception */
                    System.out.println("An error occurred: " + error.get());
                }
        );
    }
}
