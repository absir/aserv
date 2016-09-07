/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 上午9:44:01
 */
package com.absir.proto.test;

import G2.Protocol.PActivateHorseGenius;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelClass;
import com.absir.data.helper.HelperDataFormat;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.reflect.Type;

@RunWith(value = JUnit4.class)
public class UtilMsgTest {

    public void testMsg(boolean single, Object... args) throws IOException {
        if (single && args.length == 1) {
            Object arg = args[0];
            byte[] bytes = HelperDataFormat.PACK.writeAsBytes(arg);
            System.out.println(bytes.length + " : " + new String(bytes, KernelCharset.UTF8));
            Object read = HelperDataFormat.PACK.read(bytes, arg.getClass());
            System.out.println("write : " + HelperDataFormat.JSON_MAPPER.writeValueAsString(arg));
            System.out.println("read : " + HelperDataFormat.JSON_MAPPER.writeValueAsString(read));

            byte[] newBytes = HelperDataFormat.JSON.writeAsBytes(arg);
            System.out.println(newBytes.length + " : " + new String(newBytes, KernelCharset.UTF8));

        } else {
            byte[] bytes = HelperDataFormat.PACK.writeAsBytesArray(args);
            System.out.println(bytes.length + " : " + new String(bytes, KernelCharset.UTF8));
            Class[] types = KernelClass.parameterTypes(args);
            for(int i = 0; i < types.length; i++) {
                if(types[i] == null) {
                    types[i] = String.class;
                }
            }

            Object[] reads = HelperDataFormat.PACK.readArray(bytes, types);
            System.out.println("write : " + HelperDataFormat.JSON_MAPPER.writeValueAsString(args));
            System.out.println("read : " + HelperDataFormat.JSON_MAPPER.writeValueAsString(reads));
        }
    }

    @Test
    public void test() throws IOException {
        testMsg(true, 127);
        testMsg(false, 127);
        testMsg(true, "abcd");
        testMsg(false, "abcd");

        testMsg(true, null, "123");
        testMsg(true, "abcd", 1, "ddd");

        if (true) {
            return;
        }

        JavaType type = HelperDataFormat.JSON_MAPPER.constructType(PActivateHorseGenius.class);
        HelperDataFormat.JSON_MAPPER.canDeserialize(type);

        try {
            TestA testA = new TestA();
            testA.testdd = "dasdasfasd".getBytes();
            testA.name = "12333";
            testA.genius = new PActivateHorseGenius();
            testA.genius.setGeniusId(1L);
            testA.genius.setHorseId(2L);
            testA.genius.setIndex(3);

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(testA));
            byte[] buffer = new HelperDataFormat.JsonFormat(new ObjectMapper(), new JsonFactory()).writeAsBytesArray(testA, testA, testA);
            System.out.println(new String(buffer));
            Object[] testAs = new HelperDataFormat.JsonFormat(new ObjectMapper(), new JsonFactory()).readArray(buffer, TestA.class, TestA.class, TestA.class);
            System.out.println(mapper.writeValueAsString(testAs));


            byte[] dataBytes = HelperDataFormat.PACK.writeAsBytesArray(testA);


            Object[] values = HelperDataFormat.PACK.readArray(dataBytes, (Type) null);


            System.out.println(mapper.writeValueAsString(values));
            System.out.println(mapper.writeValueAsString(values[0]));

            byte[] bytes = HelperDataFormat.PACK.writeAsBytes(testA);
            for (int i = 0; i < 10; i++) {
                bytes = HelperDataFormat.PACK.writeAsBytes(testA);
            }

            System.out.println(new String(bytes));


            TestA a = (TestA) HelperDataFormat.PACK.read(bytes, TestA.class);
            for (int i = 0; i < 10; i++) {
                HelperDataFormat.PACK.read(bytes, TestA.class);
            }

            System.out.println(a.genius.getGeniusId());
            System.out.println(a.genius.getIndex());

            bytes = HelperDataFormat.PACK.writeAsBytes(testA.genius);
            System.out.println(new String(bytes));
            PActivateHorseGenius genius = (PActivateHorseGenius) HelperDataFormat.PACK.read(bytes, PActivateHorseGenius.class);
            System.out.println(genius.getGeniusId());
            System.out.println(genius.getIndex());

        } catch (Exception e) {
            e.printStackTrace();
        }
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
