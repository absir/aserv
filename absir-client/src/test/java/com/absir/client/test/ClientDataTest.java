/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月23日 下午5:45:07
 */
package com.absir.client.test;

import com.absir.data.helper.HelperDataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;

@RunWith(value = JUnit4.class)
public class ClientDataTest {

    private static String[] names;


    static {
        int length = 100;
        names = new String[length];
        for (int i = 0; i < length; i++) {
            names[i] = "abcdefg";
        }
    }

    @Test
    public void test() throws IOException {
        System.out.println(Arrays.toString(HelperDataFormat.PACK.writeAsBytes(names)));
    }

}
