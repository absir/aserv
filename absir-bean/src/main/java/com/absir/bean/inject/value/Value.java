/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午4:33:10
 */
package com.absir.bean.inject.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.absir.core.kernel.KernelLang;

/**
 * @author absir
 * 
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

	/**
	 * @return
	 */
	String value() default "";

	/**
	 * @return
	 */
	String defaultValue() default KernelLang.NULL_STRING;
}
