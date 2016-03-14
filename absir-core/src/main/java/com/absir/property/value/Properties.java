/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-6 下午2:21:11
 */
package com.absir.property.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Properties {

    /**
     * @return
     */
    Property[] value() default {};
}
