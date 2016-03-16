/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午12:40:20
 */
package com.absir.property.value;

import com.absir.property.PropertyFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

    String name() default "";

    int order() default 0;

    boolean orderProp() default true;

    int include() default 0;

    boolean includeProp() default false;

    int exclude() default 0;

    boolean excludeProp() default false;

    int ignore() default 0;

    Class<? extends PropertyFactory> factoryClass() default PropertyFactory.class;
}
