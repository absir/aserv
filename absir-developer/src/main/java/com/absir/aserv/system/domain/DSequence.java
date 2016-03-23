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
        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.HEX, System.currentTimeMillis());
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.HEX, nextSequence());
        HelperRandom.randAppendFormat(stringBuilder, 8, HelperRandom.FormatType.HEX);
        return stringBuilder.toString();
    }

    public String getNextHexId() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.HEX_DIG, System.currentTimeMillis());
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.HEX_DIG, nextSequence());
        HelperRandom.randAppendFormat(stringBuilder, 5, HelperRandom.FormatType.HEX_DIG);
        return stringBuilder.toString();
    }

}
