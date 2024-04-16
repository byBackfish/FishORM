package de.bybackfish.sql.util;

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
}
