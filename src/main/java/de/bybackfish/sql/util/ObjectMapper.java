package de.bybackfish.sql.util;

import de.bybackfish.sql.annotation.Default;
import de.bybackfish.sql.annotation.LazyLoaded;
import de.bybackfish.sql.core.DatabaseModel;
import de.bybackfish.sql.core.FishSQLException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.bybackfish.sql.util.ReflectionUtils.getTableName;

public class ObjectMapper {

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

    private final Class<? extends DatabaseModel> clazz;

    public ObjectMapper(Class<? extends DatabaseModel> clazz) {
        this.clazz = clazz;
    }

    public static boolean isNumber(Class<?> type) {
        return Number.class.isAssignableFrom(type) || NUMBER_REFLECTED_PRIMITIVES.contains(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends DatabaseModel> List<T> map(ResultSet resultSet) throws FishSQLException {
        String tableName = getTableName(clazz);
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                clazz.getDeclaredConstructor().setAccessible(true);
                T obj = (T) clazz.getDeclaredConstructor().newInstance();

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    boolean isFieldOptional = field.getType().equals(Optional.class);

                    boolean isFieldLazyLoaded = field.getType().equals(Lazy.class) && field.isAnnotationPresent(LazyLoaded.class);
                    boolean isLazyList = isFieldLazyLoaded && isListGeneric(field);

                    /*
                    System.out.println(
                            STR."""
                                    Field: \{field.getName()}
                                    isList: \{isLazyList}
                                    isFieldLazyLoaded: \{isFieldLazyLoaded}
                                    --------------------------
                                    """
                    );
                    */

                    Object value;

                    if (isFieldLazyLoaded) {
                        Class<?> targetClass;

                        if (isLazyList) {
                            targetClass = (Class<?>) ((java.lang.reflect.ParameterizedType) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getActualTypeArguments()[0];
                        } else {
                            targetClass = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        }

                        // must be ? extends DatabaseModel
                        if (!DatabaseModel.class.isAssignableFrom(targetClass)) {
                            throw new FishSQLException(STR."LazyLoaded field must be of type DatabaseModel. Received: \{targetClass.getName()}");
                        }

                        final Class<? extends DatabaseModel> targetClazz = (Class<? extends DatabaseModel>) targetClass;

                        LazyLoaded lazyLoaded = ReflectionUtils.getAnnotationFromField(field, LazyLoaded.class);
                        if (lazyLoaded == null) continue;

                        String targetFieldName = lazyLoaded.value();

                        if (isLazyList) {
                            value = Lazy.of(() -> {
                                try {
                                    return obj.linkMany(targetClazz, targetFieldName);
                                } catch (FishSQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            value = Lazy.of(() -> {
                                try {
                                    return obj.linkOne(targetClazz, targetFieldName);
                                } catch (FishSQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    } else {

                        String name = field.getName();
                        try {
                            name = field.getAnnotation(de.bybackfish.sql.annotation.Field.class).value();
                        } catch (Exception ignored) {
                        }

                        try {
                            value = resultSet.getObject(STR."\{tableName}.\{name}");
                        } catch (SQLException e) {
                            value = resultSet.getObject(name);
                        }

                        if (isFieldOptional) {
                            value = Optional.ofNullable(value);
                        }

                        if (value == null) {
                            try {
                                Default defaultValue = field.getAnnotation(Default.class);
                                value = getDefaultValue(field, defaultValue);
                            } catch (Exception ignored) {
                                value = getDefaultValue(field);
                            }
                        }

                    }

                    if (field.getType().isEnum() && value instanceof String string) {
                        Class<Enum> enumClass = (Class<Enum>) field.getType();
                        value = Enum.valueOf(enumClass, string);
                    }

                    field.set(obj, value);
                }

                list.add(obj);
            }
        } catch (Exception e) {
            throw new FishSQLException(e);
        }
        return list;
    }

    Object getDefaultValue(Field field, Default defaultValue) {
        if (isNumber(field.getType())) {
            return defaultValue.numberValue();
        } else if (field.getType().equals(Boolean.class)) {
            return defaultValue.booleanValue();
        } else if (field.getType().equals(String.class)) {
            return defaultValue.stringValue();
        }
        return null;
    }

    public Object getDefaultValue(Field field) {
        if (isNumber(field.getType())) {
            return 0;
        } else if (field.getType().equals(Boolean.class)) {
            return false;
        } else if (field.getType().equals(String.class)) {
            return "";
        }

        return null;
    }

    public static boolean isListGeneric(Field field) {
        Type fieldType = field.getGenericType();

        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0) {
                Type firstTypeArgument = typeArguments[0];
                if (firstTypeArgument instanceof ParameterizedType) {
                    ParameterizedType innerParameterizedType = (ParameterizedType) firstTypeArgument;
                    Type rawType = innerParameterizedType.getRawType();
                    if (rawType instanceof Class && List.class.isAssignableFrom((Class<?>) rawType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
