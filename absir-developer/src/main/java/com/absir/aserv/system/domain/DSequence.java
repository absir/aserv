package com.absir.aserv.system.domain;

import com.absir.aserv.system.helper.HelperRandom;

/**
 * Created by absir on 16/3/17.
 */
public class DSequence {

    private int sequence;

    public synchronized int nextSequence() {
        if (sequence >= Integer.MAX_VALUE) {
            sequence = 0;

        } else {
            sequence++;
        }

        return sequence;
    }

    public String getNextId() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.NUMBER, System.currentTimeMillis(), -1, 14);
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.NUMBER, nextSequence() % 1000, -1, 3);
        HelperRandom.randAppendFormat(stringBuilder, 3, HelperRandom.FormatType.NUMBER);
        return stringBuilder.toString();
    }

    public String getNextDigLetterId() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.DIG_LETTER, System.currentTimeMillis(), -1, 9);
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.DIG_LETTER, nextSequence() % 1296, -1, 2);
        HelperRandom.randAppendFormat(stringBuilder, 5, HelperRandom.FormatType.DIG_LETTER);
        return stringBuilder.toString();
    }

}
