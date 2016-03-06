/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-4 下午4:57:11
 */
package com.absir.system.test;

import org.junit.After;

import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryProvider;

/**
 * @author absir
 * 
 */
public class AbstractTestInject extends AbstractTest {

	/** beanFactoryProvider */
	private BeanFactoryProvider beanFactoryProvider;

	/**
	 * 
	 */
	public AbstractTestInject() {
		beanFactoryProvider = new BeanFactoryProvider(null, null, null);
		BeanDefineDiscover.open();
		beanFactoryProvider.scan(null, null, this);
		beanFactoryProvider.started();
	}

	/**
	 * 
	 */
	@After
	public void after() {
		beanFactoryProvider.stopping();
	}
}
