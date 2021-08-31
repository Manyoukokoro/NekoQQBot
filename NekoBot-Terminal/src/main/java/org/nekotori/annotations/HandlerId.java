package org.nekotori.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author: JayDeng
 * @date: 31/08/2021 15:32
 * @description:
 * @version: {@link }
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HandlerId {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
    