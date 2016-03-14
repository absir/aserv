package com.absir.core.test;

import com.absir.aserv.system.helper.HelperRandom;
import com.absir.core.helper.HelperFileName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.SecureRandom;

/**
 * Created by absir on 16/2/12.
 */
@RunWith(value = JUnit4.class)
public class FileNameTest {


    @Test
    public void test() {
        SecureRandom random = new SecureRandom();
        System.out.println(HelperRandom.randSecendId(3));
        System.out.println(HelperRandom.randSecendId(Long.MAX_VALUE, 3));
        System.out.println(HelperFileName.concat("/upload/", "/http://www.baidu.com/upload/"));
    }
}
