/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OBuff_HP extends OBuffRound<OCard> {

    // 生命恢复
    int hp;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        if (hp > 0) {
            object.treat(null, hp, null, result);

        } else {
            object.damage(null, -hp, null, result);
        }
    }

    @Override
    public void revert(OCard object, IResult result) {
    }

    @Override
    public void effect(OCard object, IResult result) {
    }
}
