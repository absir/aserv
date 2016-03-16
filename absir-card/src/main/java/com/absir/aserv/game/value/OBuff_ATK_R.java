/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-28 上午10:33:51
 */
package com.absir.aserv.game.value;

@SuppressWarnings("rawtypes")
public class OBuff_ATK_R extends OBuffRound<OCard> implements IBuffReBound<Object, OCard<OCard, OFight>> {

    // 伤害反弹比例
    private float atkR;

    public float getAtkR() {
        return atkR;
    }

    public void setAtkR(float atkR) {
        this.atkR = atkR;
    }

    @Override
    public boolean supportsFrom(Object from) {
        return true;
    }

    @Override
    public int reBound(OCard<OCard, OFight> self, OCard<OCard, OFight> target, int damage, Object damageFrom, IResult result) {
        int round = getRound();
        if (round < 0) {
            if (++round == 0) {
                result.setDone(true);
            }

            setRound(round);
        }

        int damageR = (int) (damage * atkR);
        target.damage(null, damageR, null, result);
        return damage - damageR;
    }

    @Override
    public void stepRound(OCard self, long time, int round, IResult result) {

    }

    @Override
    public void revert(OCard self, IResult result) {

    }

    @Override
    public void effect(OCard self, IResult result) {

    }

}
