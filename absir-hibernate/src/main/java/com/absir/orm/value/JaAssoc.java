/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.orm.value;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
public @interface JaAssoc {

    /**
     * 生成实体类
     */
    Class<?> entityClass();

    /**
     * 生成实体类授权
     */
    JePermission[] permissions() default {};

    /**
     * 生成实体类后缀名 默认为simpleName
     */
    String entityName() default "";

    /**
     * 生成实体类表后缀名 默认为simpleName
     */
    String tableName() default "";

    /**
     * 支持关联授权类型
     */
    Class<?>[] assocClasses() default {};

    /**
     * 通过类关联实体
     */
    Class referenceEntityClass() default Void.class;

    /**
     * 通过实体名关联实体
     */
    String referenceEntityName() default "";
}
