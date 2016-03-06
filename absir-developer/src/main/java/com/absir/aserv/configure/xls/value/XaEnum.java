/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月17日 上午10:26:43
 */
package com.absir.aserv.configure.xls.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XaEnum {

	/**
	 * @return
	 */
	public String[] value();

	/**
	 * @return
	 */
	public String[] key() default {};
}
