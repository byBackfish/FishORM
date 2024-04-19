package de.bybackfish.sql.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Default {
    int intValue() default 0;

    String stringValue() default "";

    boolean booleanValue() default false;

    long longValue() default 0;

    double doubleValue() default 0;

    float floatValue() default 0;
}
