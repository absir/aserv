/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-13 下午3:08:31
 */
package com.absir.bean.basis;

/**
 * @author absir
 * 
 */
public enum BeanScope {

	/** 单例对象 SINGLETON */
	SINGLETON,

	/** 单例延迟对象 LAZYINIT */
	LAZYINIT,

	/** 原型对象 PROTOTYPE */
	PROTOTYPE,

	/** 软引用单例 SOFTREFERENCE */
	SOFTREFERENCE,
}
