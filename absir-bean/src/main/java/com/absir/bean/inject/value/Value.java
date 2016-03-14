/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午4:33:10
 */
package com.absir.bean.inject.value;

import com.absir.core.kernel.KernelLang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    /**
     * @return
     */
    String value() default "";

    /**
     * @return
     */
    String defaultValue() default KernelLang.NULL_STRING;
}
