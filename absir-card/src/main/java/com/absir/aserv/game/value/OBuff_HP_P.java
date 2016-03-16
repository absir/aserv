/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OBuff_HP_P extends OBuffRound<OCard> {

    // 生命恢复比例
    float hpP;

    public float getHpP() {
        return hpP;
    }

    public void setHpP(float hpP) {
        this.hpP = hpP;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        if (hpP > 0) {
            object.treat(null, (int) (object.getMaxHp() * hpP), null, result);

        } else {
            object.damage(null, (int) -(object.getMaxHp() * hpP), null, result);
        }
    }

    @Override
    public void revert(OCard object, IResult result) {
    }

    @Override
    public void effect(OCard object, IResult result) {
    }

}
