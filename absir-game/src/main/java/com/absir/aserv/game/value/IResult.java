/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 上午9:44:46
 */
package com.absir.aserv.game.value;

public interface IResult {

    public boolean isDone();

    public void setDone(boolean done);

    public EResult getResult();

    public void setResult(EResult result);
}
