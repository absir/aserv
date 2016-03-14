/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:11:00
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 */
public interface IBuffTreat<T> extends IBuffFrom<T> {

    /**
     * @param hp
     * @param treatFrom
     * @param result
     * @return
     */
    public int treat(int hp, T treatFrom, IResult result);

}
