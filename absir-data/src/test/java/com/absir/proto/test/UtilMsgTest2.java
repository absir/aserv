/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 上午9:44:01
 */
package com.absir.proto.test;

import G2.Protocol.PActivateHorseGenius;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.ByteBuffer;

@RunWith(value = JUnit4.class)
public class UtilMsgTest2 {

    @Test
    public void test() throws IOException {
        byte[] bytes = "abcdefg".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 2, 0);

        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
    }

    public static class TestA {

        public PActivateHorseGenius genius;
        private byte[] testdd;
        private String name;

        public byte[] getTestdd() {
            return testdd;
        }

        public void setTestdd(byte[] testdd) {
            this.testdd = testdd;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class TestB {

        public String name123456789;
    }

}
