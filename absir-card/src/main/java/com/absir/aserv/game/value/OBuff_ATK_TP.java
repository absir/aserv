/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 上午11:25:06
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public class OBuff_ATK_TP extends OBuffRound<OCard> {

    // 提升攻击力
    private float atkTP;

    // 提升攻击力累计
    private float atkTPR = 1.0f;

    public float getAtkTP() {
        return atkTP;
    }

    public void setAtkTP(float atkTP) {
        this.atkTP = atkTP;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult buffResult) {

    }

    @Override
    public void revert(OCard object, IResult result) {
        object.setAtk((int) object.getBuffAttPR("atk", object.baseAtk(), atkTPR));
    }

    @Override
    public void effect(OCard object, IResult result) {
        atkTPR *= atkTP;
        object.setAtk((int) object.getBuffAttP("atk", object.baseAtk(), atkTP));
    }
}
