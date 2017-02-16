package com.absir.aserv.game.value;

/**
 * Created by absir on 2017/2/16.
 */
public interface ILevelCxt<T> {

    public int getLevel(T obj);

    public void levelUp(T obj, int level);

    public void setLevel(T obj, int level);

}
