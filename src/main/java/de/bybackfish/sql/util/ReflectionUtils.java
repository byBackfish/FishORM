package de.bybackfish.sql.util;

import de.bybackfish.sql.annotation.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionUtils {
    public static <T extends Annotation> Map<Field, T> getAnnotatedFields(Class<?> clazz, Class<T> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.getAnnotation(annotationClass) != null)
                .collect(Collectors.toMap(field -> field, field -> field.getAnnotation(annotationClass)));
    }

    public static <T extends Annotation> T getAnnotationFromField(Field field, Class<T> annotationClass) {
        try {
            return field.getAnnotation(annotationClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T extends Annotation> T getAnnotationFromClass(Class<?> clazz, Class<T> annotationClass) {
        try {
            return clazz.getAnnotation(annotationClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTableName(Class<?> clazz) {
        try {
            return getAnnotationFromClass(clazz, Table.class).value();
        } catch (Exception ignored) {
            return clazz.getSimpleName();
        }
    }

    public static String getFieldName(java.lang.reflect.Field field) {
        try {
            return getAnnotationFromField(field, de.bybackfish.sql.annotation.Field.class).value();
        } catch (Exception ignored) {
        }
        return field.getName();
    }
}
