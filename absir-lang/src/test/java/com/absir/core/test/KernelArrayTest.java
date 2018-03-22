package com.absir.core.test;

import com.absir.core.kernel.KernelArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(value = JUnit4.class)
public class KernelArrayTest {

    @Test
    public void test() {
        testArray(null, new int[]{2, 3, 4});
        testArray(",", new int[]{2, 3, 4});
        testArray("&^&", new int[]{});

        testArray("&^&", null);

        testArray(",", null);

        testArray("&^&", new int[]{});

        testArray("&^&", new long[]{3213, 24412});

        testArray("&^&", new String[]{"31321m", "4214241"});
    }

    public void testArray(String start, Object array) {
        String params = KernelArray.serializer(start, array);
        System.out.println(array + " => " + params);
        array = KernelArray.deserialize(start, params, array == null ? int.class : array.getClass().getComponentType());
        System.out.println(params + " <= " + array);
    }
}
