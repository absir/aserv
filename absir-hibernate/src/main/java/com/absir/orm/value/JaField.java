/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.orm.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaField {

    /**
     * 字段支持关联授权
     */
    Class<?>[] assocClasses() default {};

    /**
     * 字段支持实体类
     */
    String referenceEntityName() default "";

    /**
     * 字段支持实体名
     */
    Class<?> referencEntityClass() default void.class;
}
