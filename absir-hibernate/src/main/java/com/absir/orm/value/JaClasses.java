/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-18 上午10:33:25
 */
package com.absir.orm.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JaClasses {

	/**
	 * @return
	 */
	public Class<?> value() default void.class;

	/**
	 * @return
	 */
	public Class<?> key() default void.class;

}
