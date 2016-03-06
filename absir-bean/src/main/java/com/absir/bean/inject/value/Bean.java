/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午4:26:43
 */
package com.absir.bean.inject.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

	/**
	 * @return
	 */
	String value() default "";

	/**
	 * @return
	 */
	BeanScope scope() default BeanScope.SINGLETON;
}
