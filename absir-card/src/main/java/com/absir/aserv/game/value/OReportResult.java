/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 下午2:03:29
 */
package com.absir.aserv.game.value;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author absir
 */
public class OReportResult extends OReport implements IResult {

    /**
     * done
     */
    @JsonIgnore
    private boolean done;

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.game.value.IResult#isDone()
     */
    @Override
    public boolean isDone() {
        return done;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.game.value.IResult#setDone(boolean)
     */
    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

}
