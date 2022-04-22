package org.nekotori.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author: JayDeng
 * @date: 02/08/2021
 * @time: 16:01
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IsCommand {
    @AliasFor(annotation = Component.class)
    String value() default "";

    String[] name() default {""};

    String description() default "";
}
