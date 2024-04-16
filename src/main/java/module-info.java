module FishDatabaseLib {
    requires java.logging;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports de.bybackfish.sql.annotation;
    exports de.bybackfish.sql.core;
    exports de.bybackfish.sql.util;
    exports example;
}