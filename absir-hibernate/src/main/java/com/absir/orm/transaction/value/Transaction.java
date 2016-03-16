/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 上午11:01:06
 */
package com.absir.orm.transaction.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {

    String name() default "";

    boolean readOnly() default false;

    Class<?>[] rollback() default {};

    boolean nested() default false;

    boolean required() default false;

    int timeout() default -1;
}
