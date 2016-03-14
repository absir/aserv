/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-21 下午3:58:27
 */
package com.absir.aop.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {

    /**
     * @return
     */
    boolean jdk() default false;

    /**
     * @return
     */
    boolean impl() default true;
}
