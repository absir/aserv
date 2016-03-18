package com.absir.aserv.system.domain;

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
}
