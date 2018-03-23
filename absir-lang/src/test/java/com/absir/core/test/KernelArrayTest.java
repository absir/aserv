package com.absir.core.test;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Array;

@RunWith(value = JUnit4.class)
public class KernelArrayTest {

    boolean just = false;

    @Test
    public void test() {
        testArray(",", new String[]{"", ""});
        if (just) {
            return;
        }

        testArray(null, new byte[]{2, 3, 4});
        testArray(",", new byte[]{2, 3, 4});

        testArray(null, new char[]{'3', '5', '8'});
        testArray(",", new char[]{'3', '5', '8'});

        testArray(null, new short[]{3, 5, 8});
        testArray(",", new short[]{'3', 5, 8});

        testArray(null, new long[]{3, 5, 8});
        testArray(",", new long[]{'3', 5, 8});

        testArray(null, new float[]{3, 5.5f, 8});
        testArray(",", new float[]{'3', 5, 8.8f});

        testArray(null, new double[]{3, 5, 8.0231312});
        testArray(",", new double[]{'3', 5, 8.992312});

        testArray(null, new boolean[]{true, false, false});
        testArray(",", new boolean[]{true, false, false});

        testArray(null, new int[]{2, 3, 4});
        testArray(",", new int[]{2, 3, 4});
        testArray("&^&", new int[]{});

        testArray("", new int[]{});

        testArray("", new String[]{});

        testArray("", new String[]{""});
        testArray("", new String[]{"", ""});

        testArray(",", new String[]{""});
        testArray(",", new String[]{"", ""});

        testArray("&^&", null);

        testArray(",", null);

        testArray("&^&", new int[]{});

        testArray("&^&", new long[]{3213, 24412});

        testArray("&^&", new String[]{"31321m", "4214241"});
    }

    public void testArray(String start, Object array) {
        String params = KernelArray.serializer(start, array);
        System.out.println((array == null ? null : Array.getLength(array)) + " => " + params);
        Object newArray = KernelArray.deserialize(start, params, array == null ? int.class : array.getClass().getComponentType());
        System.out.println(params + " <= " + (array == null ? null : Array.getLength(newArray)));

        if (!KernelObject.equals(params, KernelArray.serializer(start, newArray))) {
            System.err.println("testArray  fail at " + params);
        }
    }
}
