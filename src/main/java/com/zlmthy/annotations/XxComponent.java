package com.zlmthy.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface XxComponent {

    String value() default "";
}
