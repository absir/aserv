/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
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
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JaEntity {

	/**
	 * 实体本身授权
	 * 
	 * @return
	 */
	JePermission[] permissions() default {};

	/**
	 * 实体关联扩展
	 * 
	 * @return
	 */
	JaAssoc[] jaAssoces() default {};

	/**
	 * 扩展授权设置
	 * 
	 * @return
	 */
	JaPermission[] jaPermissions() default {};
}
