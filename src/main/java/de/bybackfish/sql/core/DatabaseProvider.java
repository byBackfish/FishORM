package de.bybackfish.sql.core;

import java.util.logging.Level;

public class DatabaseProvider {
    private static FishDatabase fishDatabase;

    public static void setup(DatabaseAdapter adapter) {
        fishDatabase = new FishDatabase(adapter);
        fishDatabase.logger.setLevel(Level.INFO);
        fishDatabase.logger.log(Level.INFO, "Connected!");
    }

    public static FishDatabase getDatabase() {
        if (fishDatabase == null) return null;
        return fishDatabase;
    }
}
