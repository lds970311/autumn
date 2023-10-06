package com.autumnframework.base.stereotype;

import com.autumnframework.base.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface RestController {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
