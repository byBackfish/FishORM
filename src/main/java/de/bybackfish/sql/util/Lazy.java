package de.bybackfish.sql.util;

import java.util.function.Supplier;

public abstract class Lazy<T> {
    public abstract T get();

    public abstract boolean isLoaded();
    abstract void load();

    public static <T> Lazy<T> of(T defaultValue, Supplier<T> supplier) {
        return new Lazy<>() {
            private T value = defaultValue;
            boolean loaded = false;
            @Override
            public T get() {
                if(loaded) return value;
                load();
                return value;
            }

            @Override
            public void load() {
                value = supplier.get();
                loaded = true;
            }

            @Override
            public boolean isLoaded() {
                return loaded;
            }
        };
    }
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return of(null, supplier);
    }

    @Override
    public String toString() {
        if(isLoaded()) return get().toString();
        return "LazyLoaded{not loaded}";
    }
}