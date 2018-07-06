package com.absir.core.test;

import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.kernel.KernelUtil;
import com.absir.core.util.UtilTest;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(value = JUnit4.class)
public class KernelUtilTest {

    @Test
    public void test() {
        testMath();
    }

    protected void testMath() {
        UtilTest.spanStart();
        for (int i = 0; i < 10000; i++) {
            Math.sin(10);
        }

        System.out.println("sin span time " + UtilTest.spanTime() + "ms");

        UtilTest.spanStart();
        for (int i = 0; i < 10000; i++) {
            Math.log(10);
        }

        System.out.println("log span time " + UtilTest.spanTime() + "ms");
    }

    protected void testCompareVersion() {
        System.out.println(KernelString.transferred("\\\\\"\""));
        System.out.println(KernelString.unTransferred("\"\\\"\""));
        System.out.println(KernelString.capitalize("abc"));
        System.out.println(KernelString.capitalize("aBc"));
        //System.out.println(String.format("%dtest", 1, 2, 3));
        System.out.println(KernelDyna.toLong(" 1"));

        testCompareVersion("0", "0.0.1", -1);
        testCompareVersion("1.1", "1.0.1", 1);
        testCompareVersion("1", "1.0.1", -1);
        testCompareVersion("2", "1.0.1", 1);
        testCompareVersion("2", "10.0.1", -1);
        testCompareVersion("1.2", "1.1.100000.000.000", 1);
        testCompareVersion("2.0.01", "10.0.1", -1);
        testCompareVersion("2.0.01", "10", -1);
        testCompareVersion("2a.0.01", "2b", -1);

        for (int i = Integer.MAX_VALUE / 50; i < Integer.MAX_VALUE; i++) {
            int t = i * 50;
            if (t > 0 && t < 100) {
                System.out.println(t + " => " + i);
                break;
            }
        }
    }

    protected void testCompareVersion(String version1, String version2, int future) {
        int compare = KernelUtil.compareVersion(version1, version2);
        System.out.println(
                version1 + " " + (compare == 0 ? "=" : compare < 0 ? "<" : ">") + " " + version2 + " => " + compare);
        if (compare != future && (compare * future) < 0 || compare == 0 || future == 0) {
            Assert.fail("the future is " + future);
        }
    }

}
