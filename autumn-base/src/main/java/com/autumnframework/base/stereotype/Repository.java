package com.autumnframework.base.stereotype;

import com.autumnframework.base.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
