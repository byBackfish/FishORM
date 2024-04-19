module FishDatabaseLib {
    requires java.logging;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.xml.crypto;

    exports de.bybackfish.sql.annotation;
    exports de.bybackfish.sql.core;
    exports de.bybackfish.sql.util;
    exports example;
    exports de.bybackfish.sql.query;
}