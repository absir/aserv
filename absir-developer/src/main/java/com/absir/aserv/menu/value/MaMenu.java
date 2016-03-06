/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-12 下午2:30:14
 */
package com.absir.aserv.menu.value;

/**
 * @author absir
 * 
 */
public @interface MaMenu {

	/**
	 * @return
	 */
	String value() default "";

	/**
	 * @return
	 */
	int order() default 0;

	/**
	 * @return
	 */
	String ref() default "";

	/**
	 * @return
	 */
	String icon() default "";

}
