/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年10月7日 下午2:08:57
 */
package com.absir.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.absir.core.util.UtilPipedStream;

/**
 * @author absir
 *
 */
@RunWith(value = JUnit4.class)
public class UtilPipedStreamTest {

	@Test
	public void test() throws InterruptedException {
		System.out.println(UtilPipedStream.getHashIndex(Integer.MAX_VALUE, 1));
	}

}
