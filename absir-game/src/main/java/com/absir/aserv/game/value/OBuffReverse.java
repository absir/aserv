/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-17 下午7:41:16
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public abstract class OBuffReverse<T extends OObject> extends OBuff<T> {

    /**
     * BUFF失去
     */
    public abstract void revert(T self, IResult result);
}
