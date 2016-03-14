/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-14 下午4:26:47
 */
package com.absir.bean.basis;

import com.absir.core.base.Environment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base {

    /**
     * @return
     */
    int order() default 0;

    /**
     * @return
     */
    Environment environment() default Environment.PRODUCT;
}
