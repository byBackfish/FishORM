package de.bybackfish.sql.util;

import de.bybackfish.sql.core.DatabaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ObjectMapper {

    private final Class<? extends DatabaseModel> clazz;

    public ObjectMapper(Class<? extends DatabaseModel> clazz) {
        this.clazz = clazz;
    }

    public <T> List<T> map(ResultSet resultSet) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            clazz.getDeclaredConstructor().setAccessible(true);
            T obj = (T) clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                boolean isFieldOptional = field.getType().equals(Optional.class);

                String name = field.getName();
                try {
                    name = field.getAnnotation(de.bybackfish.sql.annotation.Field.class).value();
                } catch (Exception ignored) {
                }

                Object value = resultSet.getObject(name);

                if (isFieldOptional) {
                    value = Optional.ofNullable(value);
                }

                if (value == null) {
                    value = Optional.empty();
                }
                field.set(obj, value);
            }

            list.add(obj);
        }
        return list;
    }


}
