package example;

import de.bybackfish.sql.core.FishDatabase;

public class DatabaseProvider {
    private static FishDatabase fishDatabase;

    public static FishDatabase getDatabase() {
        if (fishDatabase == null) fishDatabase = new FishDatabase(new PostgresAdapter());
        return fishDatabase;
    }
}
