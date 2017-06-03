package com.absir.core.test;

import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.system.helper.HelperString;
import com.absir.client.helper.HelperJson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(value = JUnit4.class)
public class HelperTest {

    @Test
    public void test() throws InterruptedException, IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.NUMBER, System.currentTimeMillis(), 5, 14);
//        System.out.println(stringBuilder);
//
//        stringBuilder = new StringBuilder();
//        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.HEX, System.currentTimeMillis(), 5, 12);
//        System.out.println(stringBuilder);
//
//        stringBuilder = new StringBuilder();
//        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.DIG_LETTER, System.currentTimeMillis(), 5, 9);
//        System.out.println(stringBuilder);

        //testRandomFormatType(HelperRandom.FormatType.NUMBER);
        //testRandomFormatType(HelperRandom.FormatType.HEX);
        //testRandomFormatType(HelperRandom.FormatType.DIG_LETTER);


        System.out.println(HelperJson.encode(HelperString.split("a=b&c=d", "=&")));
        DSequence sequence = new DSequence();
        for (int i = 0; i <= 1296; i++) {
            System.out.println(sequence.getNextId());
            //System.out.println(sequence.getNextDigLetterId());
        }

        //System.out.println(Math.);
    }

    protected void testRandomFormatType(HelperRandom.IFormatType formatType) {
        System.out.println("test Integer : [" + formatType + "]");
        System.out.println(String.copyValueOf(formatType.charsForInt(Integer.MAX_VALUE)));
        System.out.println(String.copyValueOf(formatType.charsForInt(Integer.MIN_VALUE)));

        System.out.println(String.copyValueOf(formatType.charsForInt(Integer.MIN_VALUE + 1)));

        System.out.println(String.copyValueOf(formatType.charsForInt(0)));

        System.out.println("test Long : [" + formatType + "]");
        System.out.println(String.copyValueOf(formatType.charsForLong(Long.MAX_VALUE)));
        System.out.println(String.copyValueOf(formatType.charsForLong(Long.MIN_VALUE)));
        System.out.println(String.copyValueOf(formatType.charsForLong(Long.MIN_VALUE + 1)));

        System.out.println(String.copyValueOf(formatType.charsForLong(0)));
    }

}
