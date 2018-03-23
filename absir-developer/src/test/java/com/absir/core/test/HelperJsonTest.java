package com.absir.core.test;

import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelArray;
import com.absir.core.util.UtilTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(value = JUnit4.class)
public class HelperJsonTest {

    @Test
    public void test() throws InterruptedException, IOException {
        //testArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

        int length = 1000;


        String[] names = new String[length];
        for (int i = 0; i < names.length; i++) {
            names[i] = "name:" + i;
        }

        testArray(names);

        int[] ints = new int[length];
        for (int i = 0; i < names.length; i++) {
            ints[i] = i;
        }

        testArray(ints);
        //testArray(new long[]{1, 2, 3});
    }

    protected void testArray(Object array) throws IOException {
        int testTimes = 10000;
        {
            if (array instanceof String[]) {
                UtilTest.spanStart();
                for (int i = 0; i < testTimes; i++) {
                    StringUtils.join((String[]) array, ',');
                }

                System.out.println("StringUtils.join serializer " + testTimes + " span " + UtilTest.spanTime() + "ms");
            }

            if (array instanceof int[]) {
                UtilTest.spanStart();
                for (int i = 0; i < testTimes; i++) {
                    StringUtils.join((int[]) array, ',');
                }

                System.out.println("StringUtils.join serializer " + testTimes + " span " + UtilTest.spanTime() + "ms");
            }
        }
        {

            UtilTest.spanStart();
            for (int i = 0; i < testTimes; i++) {
                KernelArray.serializer(",", array);
            }

            System.out.println("Array serializer " + testTimes + " span " + UtilTest.spanTime() + "ms");

            String params = KernelArray.serializer(",", array);
            System.out.println(params);
            Class<?> arrayClass = array.getClass();
            UtilTest.spanStart();
            for (int i = 0; i < testTimes; i++) {
                KernelArray.deserialize(",", params, arrayClass.getComponentType());
            }

            System.out.println("Array deserialize " + testTimes + " span " + UtilTest.spanTime() + "ms");
        }

        {

            UtilTest.spanStart();
            for (int i = 0; i < testTimes; i++) {
                HelperJson.encode(array);
            }

            System.out.println("json encode " + testTimes + " span " + UtilTest.spanTime() + "ms");

            String json = HelperJson.encode(array);
            System.out.println(json);
            Class<?> arrayClass = array.getClass();
            UtilTest.spanStart();
            for (int i = 0; i < testTimes; i++) {
                HelperJson.decode(json, arrayClass);
            }

            System.out.println("json decode " + testTimes + " span " + UtilTest.spanTime() + "ms");
        }


    }

}
