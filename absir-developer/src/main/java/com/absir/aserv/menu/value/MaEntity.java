/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-12 下午2:35:42
 */
package com.absir.aserv.menu.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 * 
 */
@Target(value = { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaEntity {

	/**
	 * @return
	 */
	boolean closed() default false;

	/**
	 * @return
	 */
	MaMenu[] parent() default {};

	/**
	 * @return
	 */
	String name() default "";

	/**
	 * @return
	 */
	MaMenu value() default @MaMenu;
}
