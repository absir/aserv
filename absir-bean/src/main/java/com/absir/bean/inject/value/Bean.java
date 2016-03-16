/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午4:26:43
 */
package com.absir.bean.inject.value;

import com.absir.bean.basis.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    String value() default "";

    BeanScope scope() default BeanScope.SINGLETON;
}
