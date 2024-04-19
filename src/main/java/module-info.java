module FishDatabaseLib {
    requires java.logging;
    requires java.sql;

    exports de.bybackfish.sql.annotation;
    exports de.bybackfish.sql.core;
    exports de.bybackfish.sql.util;
    exports de.bybackfish.sql.query;
}