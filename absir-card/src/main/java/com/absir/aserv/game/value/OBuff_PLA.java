/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class OBuff_PLA extends OBuffRound<OCard> {

	// 暂停回合数
	public int pla;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.OBuffRound#stepRound(com.absir.aserv.game
	 * .value.OObject, long, int, com.absir.aserv.game.value.OResult)
	 */
	/**
	 * @return the pla
	 */
	public int getPla() {
		return pla;
	}

	/**
	 * @param pla
	 *            the pla to set
	 */
	public void setPla(int pla) {
		this.pla = pla;
	}

	@Override
	public void stepRound(OCard object, long time, int round, IResult result) {
		--pla;
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
		if (pla > 0 && pla < object.paused) {
			object.paused -= pla;
		}
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
		pla = getRound();
		object.paused += pla;
	}
}
