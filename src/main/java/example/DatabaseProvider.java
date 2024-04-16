package example;

import de.bybackfish.sql.core.FishDatabase;

import java.util.logging.Level;

public class DatabaseProvider {
    private static FishDatabase fishDatabase;

    public static FishDatabase getDatabase() {
        if (fishDatabase == null) {
            fishDatabase = new FishDatabase(new PostgresAdapter());
            fishDatabase.logger.setLevel(Level.INFO);
            fishDatabase.logger.log(Level.INFO, "Connected!");
        }

        return fishDatabase;
    }
}
