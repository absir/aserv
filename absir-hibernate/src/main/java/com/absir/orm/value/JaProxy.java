/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-17 下午12:30:35
 */
package com.absir.orm.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包类@Entity,@MappedSuperclass为代理实现
 * 
 * @author absir
 * 
 */
@Target({ ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JaProxy {

}
