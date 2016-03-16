/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午9:22:47
 */
package com.absir.orm.value;

import javax.persistence.Index;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaColum {

    String comment() default "";

    String defaultValue() default "`";

    String sqlType() default "";

    int length() default 0;

    Index[] indexs() default {};

    boolean foreignKey() default true;
}
