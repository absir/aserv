/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.orm.value;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author absir
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JaPermission {

	/**
	 * 实体名称
	 * 
	 * @return
	 */
	String entityName() default "";

	/**
	 * 实体支持授权
	 * 
	 * @return
	 */
	JePermission[] permissions() default {};
}