/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

@SuppressWarnings({"rawtypes"})
public class OBuff_PLA extends OBuffRound<OCard> {

    // 暂停回合数
    public int pla;

    public int getPla() {
        return pla;
    }

    public void setPla(int pla) {
        this.pla = pla;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        --pla;
    }

    @Override
    public void revert(OCard object, IResult result) {
        if (pla > 0 && pla < object.paused) {
            object.paused -= pla;
        }
    }

    @Override
    public void effect(OCard object, IResult result) {
        pla = getRound();
        object.paused += pla;
    }
}
