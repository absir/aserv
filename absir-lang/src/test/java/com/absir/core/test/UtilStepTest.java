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

import com.absir.core.util.UtilStep;
import com.absir.core.util.UtilStep.IStep;

/**
 * @author absir
 *
 */
@RunWith(value = JUnit4.class)
public class UtilStepTest {

	UtilStep step = UtilStep.openUtilStep(true, "test", 3000);

	@Test
	public void test() throws InterruptedException {
		step.addStep(new IStep() {

			@Override
			public boolean stepDone(long contextTime) {
				// TODO Auto-generated method stub
				System.out.println("A=>" + contextTime);
				return true;
			}
		});

		step.addStep(new IStep() {

			@Override
			public boolean stepDone(long contextTime) {
				// TODO Auto-generated method stub
				System.out.println("B=>" + contextTime);
				return false;
			}
		});

		step.addStep(new IStep() {

			@Override
			public boolean stepDone(long contextTime) {
				// TODO Auto-generated method stub
				System.out.println("C=>" + contextTime);
				return false;
			}
		});

		Thread.sleep(10000);
	}

}
