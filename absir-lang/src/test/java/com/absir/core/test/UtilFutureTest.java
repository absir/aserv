package com.absir.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.absir.core.util.UtilFuture;
import com.absir.core.util.UtilTest;

import junit.framework.Assert;

@RunWith(value = JUnit4.class)
public class UtilFutureTest {

	@Test
	public void test() {
		UtilTest.spanStart();
		final long delayTime = 1000;
		final UtilFuture<Object> future1 = new UtilFuture<Object>();
		final UtilFuture<Object> future2 = new UtilFuture<Object>();
		new Thread() {
			public void run() {
				future1.getBean();
				future2.setBean("future2 end");
			};

		}.start();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(delayTime);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				future1.setBean("future1 end");
			};

		}.start();

		System.out.println(future2.getBean(10000));
		long spanTime = UtilTest.spanTime();
		System.out.println(getClass() + " test at " + spanTime + " ms");
		if (spanTime < delayTime || spanTime > (delayTime + 1000)) {
			Assert.fail("this future is " + delayTime);
		}
	}
}
