package example;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.core.DatabaseProvider;
import de.bybackfish.sql.core.FishDatabase;
import de.bybackfish.sql.core.FishSQLException;
import de.bybackfish.sql.query.AbstractQueryBuilder;
import de.bybackfish.sql.query.QueryBuilder;
import de.bybackfish.sql.query.SelectQueryBuilder;
import de.bybackfish.sql.query.WhereQueryBuilder;
import example.model.Employee;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.Map;

public class Update {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        DatabaseProvider.setup(new PostgresAdapter());
        FishDatabase fishDatabase = DatabaseProvider.getDatabase();

        fishDatabase.connect(new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password"));

        SelectQueryBuilder selectQueryBuilder = QueryBuilder.select("*");
        selectQueryBuilder.where(where ->
                where.and("id = ?", 3)
        );

        /* Or alternatively, using the static method ById
        * FishDatabase.SelectOptions selectOptions = new FishDatabase.SelectOptions("*")
        *        .withFilter(FishDatabase.FilterOptions.ById(5));
        */


        Employee.findOne(Employee.class, selectQueryBuilder).ifPresent(employee -> {
            employee.name = "John Doe";
            try {
                employee.update();
            } catch (FishSQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
