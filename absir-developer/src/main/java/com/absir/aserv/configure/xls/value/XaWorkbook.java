/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-26 下午1:24:37
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
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface XaWorkbook {

	/**
	 * @return
	 */
	String workbook() default "";

	/**
	 * @return
	 */
	int[] sheets() default { 0 };
}
