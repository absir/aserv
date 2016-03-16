/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 下午2:03:29
 */
package com.absir.aserv.game.value;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OReportResult extends OReport implements IResult {

    @JsonIgnore
    private boolean done;

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

}
