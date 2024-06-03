package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.DatabaseProvider;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;
import de.bybackfish.sql.query.QueryBuilder;
import de.bybackfish.sql.query.SelectQueryBuilder;
import example.model.Employee;

import java.util.List;

public class Select {

  public static void main() throws FishSQLException, ClassNotFoundException {
    DatabaseProvider.setup(new PostgresAdapter());
    FishDatabase fishDatabase = DatabaseProvider.getDatabase();

    fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));

    Employee firstEmployee = Employee.findOne(Employee.class, QueryBuilder.select("*")).get();
    System.out.println(firstEmployee.name);

    List<Employee> all = Employee.all(Employee.class);
    for (Employee e : all) {
      System.out.println(e.type);
    }

    SelectQueryBuilder selectQueryBuilder = QueryBuilder.select("*");
    selectQueryBuilder.where(where -> where.and("id = ?", 3));

    Employee.findOne(Employee.class, selectQueryBuilder).ifPresent(employee -> {
      System.out.println(employee.name);
    });
  }
}
