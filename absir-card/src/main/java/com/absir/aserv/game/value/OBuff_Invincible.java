/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-6 下午9:07:41
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class OBuff_Invincible extends OBuffRound<OCard> {

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.OBuff#against(com.absir.aserv.game.value
	 * .OBuff)
	 */
    /*
     * public int against(OBuff buff) { if (getClass() != buff.getClass()) {
	 * return 0; }
	 * 
	 * return getRound() >= ((OBuff_Invincible) buff).getRound() ? 1 : -1; }
	 */

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuffRound#stepRound(com.absir.aserv.game
     * .value.OObject, long, int, com.absir.aserv.game.value.IResult)
     */
    @Override
    public void stepRound(OCard self, long time, int round, IResult buffResult) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuffReverse#revert(com.absir.aserv.game
     * .value.OObject, com.absir.aserv.game.value.IResult)
     */
    @Override
    public void revert(OCard self, IResult result) {
        self.setInvincible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.game.value.OBuff#effect(com.absir.aserv.game.value
     * .OObject, com.absir.aserv.game.value.IResult)
     */
    @Override
    public void effect(OCard self, IResult result) {
        self.setInvincible(true);
    }

}
