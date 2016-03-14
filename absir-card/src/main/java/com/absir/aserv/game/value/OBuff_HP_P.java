/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OBuff_HP_P extends OBuffRound<OCard> {

    // 生命恢复比例
    float hpP;

    /**
     * @return the hpP
     */
    public float getHpP() {
        return hpP;
    }

    /**
     * @param hpP the hpP to set
     */
    public void setHpP(float hpP) {
        this.hpP = hpP;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuffRound#stepRound(com.absir.aserv.game
     * .value.OObject, long, int, com.absir.aserv.game.value.OResult)
     */
    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        if (hpP > 0) {
            object.treat(null, (int) (object.getMaxHp() * hpP), null, result);

        } else {
            object.damage(null, (int) -(object.getMaxHp() * hpP), null, result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuffReverse#revert(com.absir.aserv.game
     * .value.OObject, com.absir.aserv.game.value.OResult)
     */
    @Override
    public void revert(OCard object, IResult result) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuff#effect(com.absir.aserv.game.value
     * .OObject, com.absir.aserv.game.value.OResult)
     */
    @Override
    public void effect(OCard object, IResult result) {
    }

}
