package com.absir.core.test;

import com.absir.aserv.system.helper.HelperRandom;
import com.absir.client.helper.HelperEncrypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(value = JUnit4.class)
public class FileNameTest {

    @Test
    public void test() {
//        SecureRandom random = new SecureRandom();
//        System.out.println(HelperRandom.randSecondId(3));
//        System.out.println(HelperRandom.randSecondId(Long.MAX_VALUE, 3));
//        System.out.println(HelperFileName.concat("/upload/", "/http://www.baidu.com/upload/"));

        System.out.println(HelperRandom.randHashId(this));
        System.out.println(HelperRandom.randSecondId(Long.MAX_VALUE, 16, 8));
        System.out.println(HelperRandom.randSecondId(Long.MAX_VALUE, 16, 8, HelperRandom.FormatType.DIG_LETTER));
        System.out.println(HelperRandom.randSecondId(Long.MAX_VALUE, 16, 8, HelperRandom.FormatType.DIG_LETTER));

        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.DIG_LETTER, Integer.MAX_VALUE);
        System.out.println(stringBuilder.toString());


        for (HelperRandom.FormatType type : HelperRandom.FormatType.values()) {
            stringBuilder = new StringBuilder();
            HelperRandom.appendFormat(stringBuilder, type, Integer.MAX_VALUE);
            System.out.println(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            HelperRandom.appendFormatLong(stringBuilder, type, Long.MAX_VALUE);
            System.out.println(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            HelperRandom.appendFormat(stringBuilder, type, 0, 0, 3);
            System.out.println(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            HelperRandom.appendFormatLong(stringBuilder, type, 0, 0, 3);
            System.out.println(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            HelperRandom.appendFormatLongMd5(stringBuilder, type, Long.MAX_VALUE, 10);
            System.out.println("md5 = " + stringBuilder.toString());

            stringBuilder = new StringBuilder();
            HelperRandom.appendFormatLongMd5(stringBuilder, type, 0, 10);
            System.out.println("md5 = " + stringBuilder.toString());
        }


        System.out.println(HelperEncrypt.encryptionMD5("1").substring(0, 10));
        System.out.println(HelperEncrypt.encryptionMD5("2").substring(0, 10));
    }
}
