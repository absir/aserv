package com.absir.test;

import com.absir.aserv.system.helper.HelperRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by absir on 16/3/6.
 */
@RunWith(JUnit4.class)
public class HelperRandomTest {

    @Test
    public void test() {
        for (int i = 0; i < 9; i++) {
            System.out.println(HelperRandom.randSecondId(3));
        }
    }
}
