package de.bybackfish.sql.query;

import de.bybackfish.sql.core.DatabaseOptions;
import de.bybackfish.sql.util.JointClasses;
import example.DatabaseProvider;
import example.model.Department;
import example.model.Employee;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Test {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        DatabaseProvider.getDatabase().connect(
                new DatabaseOptions("localhost", 5432, "postgres", "prj1_user", "prj1_password")
        );


       /* AbstractQueryBuilder.BuiltQuery build =
                QueryBuilder.select("*").from("employee").limit(1).orderBy("id", AbstractQueryBuilder.OrderDirection.DESC)
                        .build(DatabaseProvider.getDatabase());

        List<Employee> employees = build.unwrap(Employee.class);

        System.out.println(employees.size());

        for (Employee employee : employees) {
            System.out.println(employee);
        }

        build.execute();*/

        /*QueryBuilder.delete("employee")
                .where(
                        where("id = ?", 1)
                )
                .build(DatabaseProvider.getDatabase()).execute();*/
/*
        QueryBuilder.update("employee")
                .set("name", "Maik Egbers")
                .where(
                        where("id = ?", 2)
                )
                .build(DatabaseProvider.getDatabase()).execute();

        QueryBuilder.insert("employee")
                .add("name", "Maik Egbers")
                .add("department_id", 1)
                .build(DatabaseProvider.getDatabase()).execute();*/


        SelectQueryBuilder selectQueryBuilder = QueryBuilder.select("*").from("employee");
        selectQueryBuilder.join("department", "department_id", "id");

        JointClasses.JointPair<Employee, Department> pair = selectQueryBuilder.build(DatabaseProvider.getDatabase()).unwrap(Employee.class, Department.class).get(0);

        System.out.println(pair);
    }

}
