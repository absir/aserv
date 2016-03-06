/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-12 上午9:44:01
 */
package com.absir.proto.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.absir.data.helper.HelperDatabind;
import com.fasterxml.jackson.databind.JavaType;

import G2.Protocol.PActivateHorseGenius;

/**
 * @author absir
 * 
 */
@RunWith(value = JUnit4.class)
public class UtilMsgTest {

	public static class TestA {

		private byte[] testdd;

		private String name;

		public PActivateHorseGenius genius;

		/**
		 * @return the testdd
		 */
		public byte[] getTestdd() {
			return testdd;
		}

		/**
		 * @param testdd
		 *            the testdd to set
		 */
		public void setTestdd(byte[] testdd) {
			this.testdd = testdd;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

	}

	public static class TestB {

		public String name123456789;
	}

	@Test
	public void test() {
		JavaType type = HelperDatabind.OBJECT_MAPPER.constructType(PActivateHorseGenius.class);
		HelperDatabind.OBJECT_MAPPER.canDeserialize(type);

		try {
			TestA testA = new TestA();
			testA.testdd = "dasdasfasd".getBytes();
			testA.name = "12333";
			testA.genius = new PActivateHorseGenius();
			testA.genius.setGeniusId(1L);
			testA.genius.setHorseId(2L);
			testA.genius.setIndex(3);

			byte[] bytes = HelperDatabind.writeAsBytes(testA);
			for (int i = 0; i < 10; i++) {
				bytes = HelperDatabind.writeAsBytes(testA);
			}

			System.out.println(new String(bytes));

			TestA a = (TestA) HelperDatabind.read(bytes, TestA.class);
			for (int i = 0; i < 10; i++) {
				HelperDatabind.read(bytes, TestA.class);
			}

			System.out.println(a.genius.getGeniusId());
			System.out.println(a.genius.getIndex());

			bytes = HelperDatabind.writeAsBytes(testA.genius);
			System.out.println(new String(bytes));
			PActivateHorseGenius genius = (PActivateHorseGenius) HelperDatabind.read(bytes, PActivateHorseGenius.class);
			System.out.println(genius.getGeniusId());
			System.out.println(genius.getIndex());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
