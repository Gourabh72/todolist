package org.todolist.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AppAllowed {
    String OPTIONAL_APP="*";
    String IDENTIFIED_APP="IDENTIFIED_APP";

    String[] value() default {};

    String property() default "";
}
