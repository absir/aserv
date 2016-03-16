/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-6 下午9:07:41
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public class OBuff_Invincible extends OBuffRound<OCard> {

    @Override
    public void stepRound(OCard self, long time, int round, IResult buffResult) {

    }

    @Override
    public void revert(OCard self, IResult result) {
        self.setInvincible(false);
    }

    @Override
    public void effect(OCard self, IResult result) {
        self.setInvincible(true);
    }

}
