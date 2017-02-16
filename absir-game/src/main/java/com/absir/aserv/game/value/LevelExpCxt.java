/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-15 下午1:20:31
 */
package com.absir.aserv.game.value;

public class LevelExpCxt<T extends ILevelExp> extends LevelCxt<T> implements ILevelExpCxt<T> {

    public int getExp(T obj) {
        return obj.getExp();
    }

    public void setExp(T obj, int exp) {
        obj.setExp(exp);
    }

}
