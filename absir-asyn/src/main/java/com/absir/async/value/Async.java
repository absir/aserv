/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-24 下午3:55:53
 */
package com.absir.async.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {

    /**
     * @return
     */
    long timeout() default 0;

    /**
     * @return
     */
    boolean notifier() default false;

    /**
     * @return
     */
    boolean thread() default false;
}
