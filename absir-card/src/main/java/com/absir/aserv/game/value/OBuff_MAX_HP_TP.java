/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OBuff_MAX_HP_TP extends OBuffRound<OCard> {

    // 最大生命提升
    float maxHpP;

    // 最大生命提升累计
    float maxHpPR = 1.0f;

    public float getMaxHpP() {
        return maxHpP;
    }

    public void setMaxHpP(float maxHpP) {
        this.maxHpP = maxHpP;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        maxHpPR *= maxHpP;
        int maxHp = object.getMaxHp();
        object.setMaxHp((int) object.getBuffAttP("maxHp", object.baseHp(), maxHpP));
        maxHp -= maxHp;
        if (maxHp > 0) {
            object.damage(null, maxHp, null, result);

        } else {
            object.treat(null, -maxHp, null, result);
        }
    }

    @Override
    public void revert(OCard object, IResult result) {
        object.setMaxHp((int) object.getBuffAttPR("maxHp", object.baseHp(), maxHpPR));
        if (object.getHp() > object.getMaxHp()) {
            object.setHp(object.getMaxHp());
        }
    }

    @Override
    public void effect(OCard object, IResult result) {
    }

}
