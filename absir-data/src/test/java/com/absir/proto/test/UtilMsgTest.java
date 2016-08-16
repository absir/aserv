/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-12 上午9:44:01
 */
package com.absir.proto.test;

import G2.Protocol.PActivateHorseGenius;
import com.absir.data.helper.HelperDatabind;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Type;

@RunWith(value = JUnit4.class)
public class UtilMsgTest {

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

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writeValueAsString(testA));

            

            byte[] dataBytes = HelperDatabind.writeAsBytesArray(testA);
            Object[] values = HelperDatabind.readArray(dataBytes, (Type) null);

            System.out.println(mapper.writeValueAsString(values));
            System.out.println(mapper.writeValueAsString(values[0]));

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
