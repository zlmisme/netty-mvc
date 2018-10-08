package com.zlmthy.annotations;


import com.zlmthy.enums.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapper {

    String value() default "/";

    RequestMethod[] method() default {RequestMethod.GET,RequestMethod.POST};
}
