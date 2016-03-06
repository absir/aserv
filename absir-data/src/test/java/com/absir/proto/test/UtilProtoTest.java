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

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

import G2.Protocol.PActivateHorseGenius;

/**
 * @author absir
 * 
 */
@RunWith(value = JUnit4.class)
public class UtilProtoTest {

	public static class TestA {

		private byte[] testdd;

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

	}

	public static class TestB {

		public PActivateHorseGenius genius;
	}

	@Test
	public void test() throws Exception {
		PActivateHorseGenius activateHorseGenius = new PActivateHorseGenius();
		activateHorseGenius.setHorseId(0L);
		activateHorseGenius.setGeniusId(0L);
		activateHorseGenius.setIndex(2);
		Codec<PActivateHorseGenius> codec = ProtobufProxy.create(PActivateHorseGenius.class);
		byte[] bytes = codec.encode(activateHorseGenius);
		System.out.println(bytes);
		PActivateHorseGenius genius = codec.decode(bytes);
		System.out.println(genius.getIndex());
		genius.clearDirty();
		genius.setIndex(3);
		for (int i = 0; i < 3; i++) {
			System.out.println(genius.isDirtyI(i));
		}

		for (int i = 32; i < 145; i += 2) {
			genius.setDirtyI(i);
		}

		for (int i = 32; i < 145; i++) {
			System.out.println(genius.isDirtyI(i));
		}

		genius.clearDirty();
		for (int i = 32; i < 145; i++) {
			System.out.println(genius.isDirtyI(i));
		}
	}

}
