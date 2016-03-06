/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-24 下午1:37:53
 */
package com.absir.context.schedule.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

	/**
	 * @return
	 */
	public String cron() default "";

	/**
	 * @return
	 */
	public long fixedDelay() default 0;

	/**
	 * @return
	 */
	public long initialDelay() default 0;
}
