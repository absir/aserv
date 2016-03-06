/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-8 下午4:13:43
 */
package com.absir.system.test.lang;

import org.junit.Test;

import com.absir.aop.AopProxyUtils;
import com.absir.system.test.AbstractTest;

/**
 * @author absir
 * 
 */
public class TestLoader extends AbstractTest {

	/**
	 * @return
	 */
	public static String getName() {
		return "123";
	}

	public static abstract class TestAop {

		public String getName() {
			return "123";
		}
	}

	@Test
	public void test() {
		TestAop testAop = (TestAop) AopProxyUtils.getProxy(null, TestAop.class, null, false, true);
		System.out.println(testAop.getName());

	}

	public static void main(String... args) throws Throwable {
		new TestLoader().test();
	}
}
