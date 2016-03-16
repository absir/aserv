/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:15:08
 */
package com.absir.aserv.game.value;

public interface IBuffTreatBoost<T> extends IBuffFrom<T> {

    public int boost(int hp, T treatFrom, IResult result);

}
