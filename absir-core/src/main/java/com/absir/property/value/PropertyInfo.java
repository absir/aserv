/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-6 下午2:21:37
 */
package com.absir.property.value;

/**
 * @author absir
 */
public @interface PropertyInfo {

    /**
     * @return
     */
    Class<?> value();

    /**
     * @return
     */
    String valueInfo() default "";
}
