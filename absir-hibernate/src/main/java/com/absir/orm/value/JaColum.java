/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-20 下午9:22:47
 */
package com.absir.orm.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Index;

/**
 * @author absir
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JaColum {

	/**
	 * @return
	 */
	String comment() default "";

	/**
	 * @return
	 */
	String defaultValue() default "`";

	/**
	 * @return
	 */
	String sqlType() default "";

	/**
	 * @return
	 */
	int length() default 0;

	/**
	 * @return
	 */
	Index[] indexs() default {};

	/**
	 * @return
	 */
	boolean foreignKey() default true;
}
