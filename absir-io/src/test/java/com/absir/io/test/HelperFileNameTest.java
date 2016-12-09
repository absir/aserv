/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月11日 下午3:37:14
 */
package com.absir.io.test;

import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelDyna;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URL;

@RunWith(value = JUnit4.class)
public class HelperFileNameTest {

    @Test
    public void test() {
        System.out.println(HelperFileName.getClassPath(null));
        System.out.println(HelperFileName.getClassPath(getClass()));
        System.out.println(HelperFileName.getClassPath(KernelDyna.class));
        System.out.println(HelperFileName.getClassPath(String.class));
        System.out.println(String.class.getResource(""));


    }

    protected void dump(URL url) {
    }
}
