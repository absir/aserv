/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 下午3:56:59
 */
package com.absir.context.core.compare.value;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author absir
 * 
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RUNTIME)
public @interface CaFilter {

}
