/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 下午2:01:54
 */
package com.absir.aserv.game.value;

public class OResult implements IResult {

    private boolean done;

    private EResult result;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public EResult getResult() {
        return result;
    }

    public void setResult(EResult result) {
        this.result = result;
    }
}
