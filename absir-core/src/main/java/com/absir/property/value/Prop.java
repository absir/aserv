/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-7 下午12:40:20
 */
package com.absir.property.value;

import com.absir.property.PropertyFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

    /**
     * @return
     */
    String name() default "";

    /**
     * @return
     */
    int order() default 0;

    /**
     * @return
     */
    boolean orderProp() default true;

    /**
     * @return
     */
    int include() default 0;

    /**
     * @return
     */
    boolean includeProp() default false;

    /**
     * @return
     */
    int exclude() default 0;

    /**
     * @return
     */
    boolean excludeProp() default false;

    /**
     * @return
     */
    int ignore() default 0;

    /**
     * @return
     */
    Class<? extends PropertyFactory> factoryClass() default PropertyFactory.class;
}
