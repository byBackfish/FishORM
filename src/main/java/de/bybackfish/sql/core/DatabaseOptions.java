package de.bybackfish.sql.core;

public record DatabaseOptions(
        String host,
        int port,
        String database,
        String username,
        String password
) {
}
