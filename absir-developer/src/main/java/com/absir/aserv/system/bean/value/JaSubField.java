/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-16 上午11:35:12
 */
package com.absir.aserv.system.bean.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.absir.aserv.system.crud.SubCrudFactory;

/**
 * @author absir
 * 
 */
@JaCrud(factory = SubCrudFactory.class, parameters = { "" })
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JaSubField {

	/**
	 * @return
	 */
	String value();
}
