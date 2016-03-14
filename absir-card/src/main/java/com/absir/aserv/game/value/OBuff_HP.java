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
public class OBuff_HP extends OBuffRound<OCard> {

    // 生命恢复
    int hp;

    /**
     * @return the hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(int hp) {
        this.hp = hp;
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
        if (hp > 0) {
            object.treat(null, hp, null, result);

        } else {
            object.damage(null, -hp, null, result);
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
