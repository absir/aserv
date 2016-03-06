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
@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
public @interface JaAssoc {

	/**
	 * 生成实体类
	 * 
	 * @return
	 */
	Class<?> entityClass();

	/**
	 * 生成实体类授权
	 * 
	 * @return
	 */
	JePermission[] permissions() default {};

	/**
	 * 生成实体类后缀名 默认为simpleName
	 * 
	 * @return
	 */
	String entityName() default "";

	/**
	 * 生成实体类表后缀名 默认为simpleName
	 * 
	 * @return
	 */
	String tableName() default "";

	/**
	 * 支持关联授权类型
	 * 
	 * @return
	 */
	Class<?>[] assocClasses() default {};

	/**
	 * 通过类关联实体
	 * 
	 * @return
	 */
	Class referenceEntityClass() default Void.class;

	/**
	 * 通过实体名关联实体
	 * 
	 * @return
	 */
	String referenceEntityName() default "";
}
