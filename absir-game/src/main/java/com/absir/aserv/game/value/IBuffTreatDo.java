/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:16:42
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public interface IBuffTreatDo<T, O extends OObject> extends IBuffFrom<T> {

    public void treatDo(O self, O target, int treat, T treatFrom, IResult result);

}
