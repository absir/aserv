/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-17 下午7:45:18
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public abstract class OBuffRound<T extends OObject> extends OBuffReverse<T> {

    // 回合数
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public final void step(T self, long time, IResult result) {
        if (round > 0) {
            if (--round == 0) {
                result.setDone(true);
            }
        }

        stepRound(self, time, round, result);
    }

    public abstract void stepRound(T self, long time, int round, IResult result);
}
