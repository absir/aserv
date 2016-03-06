/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-5 上午11:10:44
 */
package com.absir.aserv.menu.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.absir.aserv.menu.IMenuFactory;
import com.absir.aserv.menu.OMenuFactory;

/**
 * @author absir
 * 
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaFactory {

	/**
	 * @return
	 */
	String value() default "";

	/**
	 * @return
	 */
	Class<? extends IMenuFactory> factory() default OMenuFactory.class;

	/**
	 * @return
	 */
	MaMenu[] parent() default {};

	/**
	 * @return
	 */
	MaMenu menu() default @MaMenu;
}
