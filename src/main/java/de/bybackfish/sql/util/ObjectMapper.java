package de.bybackfish.sql.util;

import de.bybackfish.sql.annotation.Default;
import de.bybackfish.sql.core.DatabaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.bybackfish.sql.util.ReflectionUtils.getTableName;

public class ObjectMapper {

    private final Class<? extends DatabaseModel> clazz;

    public ObjectMapper(Class<? extends DatabaseModel> clazz) {
        this.clazz = clazz;
    }

    public <T> List<T> map(ResultSet resultSet) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String tableName = getTableName(clazz);
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
                } catch (Exception ignored) {}



                Object value;
                try {
                    value = resultSet.getObject(STR."{tableName}.{name}");
                } catch (SQLException e) {
                    value = resultSet.getObject(name);
                }

                if (isFieldOptional) {
                    value = Optional.ofNullable(value);
                }

                if(value == null) {
                    try{
                        Default defaultValue = field.getAnnotation(Default.class);
                        value = getDefaultValue(field, defaultValue);
                    } catch (Exception ignored){
                        value = getDefaultValue(field);
                    }
                }

                field.set(obj, value);
            }

            list.add(obj);
        }
        return list;
    }

    Object getDefaultValue(Field field, Default defaultValue) {
        if (isNumber(field.getType())) {
            return defaultValue.intValue();
        } else if(field.getType().equals(Boolean.class)) {
            return defaultValue.booleanValue();
        }
        else if(field.getType().equals(String.class)) {
            return defaultValue.stringValue();
        }
        return null;
    }

    public Object getDefaultValue(Field field) {
        if(isNumber(field.getType())) {
            return 0;
        }
        else if(field.getType().equals(Boolean.class)) {
            return false;
        }
        else if(field.getType().equals(String.class)) {
            return "";
        }

        return null;
    }


    private final static Set<Class<?>> NUMBER_REFLECTED_PRIMITIVES;
    static {
        Set<Class<?>> s = new HashSet<>();
        s.add(byte.class);
        s.add(short.class);
        s.add(int.class);
        s.add(long.class);
        s.add(float.class);
        s.add(double.class);
        NUMBER_REFLECTED_PRIMITIVES = s;
    }

    public static boolean isNumber(Class<?> type) {
        return Number.class.isAssignableFrom(type) || NUMBER_REFLECTED_PRIMITIVES.contains(type);
    }
}
