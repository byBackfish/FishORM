package de.bybackfish.sql.core;

public record DatabaseOptions(
        String host,
        int port,
        String schema,
        String database,
        String username,
        String password
) {
}
