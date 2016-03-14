/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-12 下午4:40:27
 */
package com.absir.aserv.system.server.value;

import com.absir.core.kernel.KernelLang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 *
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Result {

    /**
     * @return
     */
    String name() default KernelLang.NULL_STRING;

    /**
     * @return
     */
    boolean validation() default true;

    /**
     * @return
     */
    int group() default 0;
}
