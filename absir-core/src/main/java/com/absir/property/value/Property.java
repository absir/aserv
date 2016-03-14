/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-6 下午2:21:37
 */
package com.absir.property.value;

/**
 * @author absir
 */
public @interface Property {

    /**
     * @return
     */
    String name() default "";

    /**
     * @return
     */
    String beanName() default "";

    /**
     * @return
     */
    Prop[] props() default {};

    /**
     * @return
     */
    PropertyInfo[] infos() default {};
}
