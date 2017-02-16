package com.absir.aserv.game.value;

/**
 * Created by absir on 2017/2/16.
 */
public interface ILevelExpCxt<T> extends ILevelCxt<T> {

    public int getExp(T obj);

    public void setExp(T obj, int exp);

}
