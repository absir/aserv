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

@Retention(RetentionPolicy.RUNTIME)
public @interface JaPermission {

    /**
     * 实体名称
     */
    String entityName() default "";

    /**
     * 实体支持授权
     */
    JePermission[] permissions() default {};
}