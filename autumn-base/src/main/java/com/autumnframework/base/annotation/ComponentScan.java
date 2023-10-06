package com.autumnframework.base.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    @AliasFor("basePackages")
    String value() default "";

    @AliasFor("value")
    String[] basePackages() default {};
}
