package com.absir.client.test;

import com.absir.client.helper.HelperEncrypt;
import com.absir.core.util.UtilTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by absir on 2017/3/21.
 */
@RunWith(value = JUnit4.class)
public class EncryptBuffTest {

    @Test
    public void test() throws IOException {
        testSROREncrypt("nndsd1232", "0a8c0");
//        testSROREncrypt("a", "123");
//        testSROREncrypt("b", "123");
//        testSROREncrypt("b", "124");
        //testSRORTime("dsadasfrq21321", "散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的散打王地区24212131231adadasda啊大大大撒的撒打算的", 100000, true);
    }

    protected void testSRORTime(String key, String inStr, int cnt, boolean decrypt) {
        byte[] sKey = HelperEncrypt.getSROREncryptKey(key);
        byte[] bytes = inStr.getBytes();
        byte[] inBytes;
        UtilTest.spanStart();
        for (int i = 0; i < cnt; i++) {
            inBytes = HelperEncrypt.encryptSRORKey(bytes, 1, 5, sKey);
            if (decrypt) {
                HelperEncrypt.decryptSRORKey(inBytes, 1, 5, sKey);
            }
        }

        System.out.println("testSRORTime = " + inStr.length() + " =》 " + UtilTest.spanTime());
    }

    protected void testSROREncrypt(String key, String inStr) {
        byte[] sKey = HelperEncrypt.getSROREncryptKey(key);
        System.out.println(Arrays.toString(sKey));

        System.out.println(inStr);

        byte[] bytes = inStr.getBytes();

        System.out.println(Arrays.toString(bytes));

        bytes = HelperEncrypt.encryptSRORKey(bytes, 1, 4, sKey);

        System.out.println(Arrays.toString(bytes));

        bytes = HelperEncrypt.decryptSRORKey(bytes, 1, 4, sKey);

        System.out.println(new String(bytes));
    }

}
