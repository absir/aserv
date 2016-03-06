/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 下午2:21:37
 */
package com.absir.property.value;

/**
 * @author absir
 * 
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
