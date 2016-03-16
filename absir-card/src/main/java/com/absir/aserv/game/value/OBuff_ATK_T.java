/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 上午10:26:40
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public class OBuff_ATK_T extends OBuffRound<OCard> {

    // 提升攻击力
    private int atkT;

    // 攻击提升累计
    private int atkTR;

    public int getAtkT() {
        return atkT;
    }

    public void setAtkT(int atkT) {
        this.atkT = atkT;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        atkTR += atkT;
        object.setAtk((int) object.getBuffAtt("atk", object.baseAtk(), atkT));
    }

    @Override
    public void revert(OCard object, IResult result) {
        object.setAtk((int) object.getBuffAtt("atk", object.baseAtk(), -atkTR));
    }

    @Override
    public void effect(OCard object, IResult result) {
    }

}
