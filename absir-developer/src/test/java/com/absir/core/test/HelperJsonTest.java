package com.absir.core.test;

import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelArray;
import com.absir.core.util.UtilTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(value = JUnit4.class)
public class HelperJsonTest {

    @Test
    public void test() throws InterruptedException, IOException {
        //testArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

        String[] names = new String[1000];
        for (int i = 0; i < names.length; i++) {
            names[i] = "name:" + i;
        }

        testArray(names);

        int[] ints = new int[1000];
        for (int i = 0; i < names.length; i++) {
            ints[i] = i;
        }

        testArray(ints);

        //testArray(new long[]{1, 2, 3});
    }

    protected void testArray(Object array) throws IOException {
        int testTimes = 10000;

        UtilTest.spanStart();
        for (int i = 0; i < testTimes; i++) {
            String params = KernelArray.serializer(",", array);
            Object dArray = KernelArray.deserialize(",", params, array.getClass());
        }

        System.out.println("KernelArray testTimes " + testTimes + " span " + UtilTest.spanTime() + "ms");

        UtilTest.spanStart();

        for (int i = 0; i < testTimes; i++) {
            String json = HelperJson.encode(array);
            Object dArray = HelperJson.decode(json, array.getClass());
        }

        System.out.println("json testTimes " + testTimes + " span " + UtilTest.spanTime() + "ms");


    }

}
