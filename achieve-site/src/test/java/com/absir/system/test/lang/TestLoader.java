/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午4:13:43
 */
package com.absir.system.test.lang;

import com.absir.aop.AopProxyUtils;
import com.absir.system.test.AbstractTest;
import org.junit.Test;

public class TestLoader extends AbstractTest {

    public static String getName() {
        return "123";
    }

    public static void main(String... args) throws Throwable {
        new TestLoader().test();
    }

    @Test
    public void test() {
        TestAop testAop = (TestAop) AopProxyUtils.getProxy(null, TestAop.class, null, false, true);
        System.out.println(testAop.getName());
        String str = "\">" + "${entity.";


    }

    public static abstract class TestAop {

        public String getName() {
            return "123";
        }
    }
}
