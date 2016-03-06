/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 下午6:11:48
 */
package com.absir.core.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author absir
 *
 */
@RunWith(value = JUnit4.class)
public class MapTest {

	@Test
	public void test() throws InterruptedException {
		final Map<Object, Object> map = new HashMap<Object, Object>();
		final Random random = new Random(new Date().getTime());
		final AtomicInteger cnt = new AtomicInteger(0);
		for (int i = 0; i < 0; i++) {
			new Thread() {

				public void run() {
					for (int i = 0; i < 1000; i++) {
						System.out.println("map == " + map.size() + " : " + cnt.incrementAndGet());
						map.put(random.nextInt(100), random.nextInt(100));
						map.remove(random.nextInt(100));
					}

				};

			}.start();
		}

		Thread.sleep(30000);
	}

}
