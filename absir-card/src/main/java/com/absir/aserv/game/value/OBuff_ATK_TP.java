/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-22 上午11:25:06
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class OBuff_ATK_TP extends OBuffRound<OCard> {

	// 提升攻击力
	private float atkTP;

	// 提升攻击力累计
	private float atkTPR = 1.0f;

	/**
	 * @return the atkTP
	 */
	public float getAtkTP() {
		return atkTP;
	}

	/**
	 * @param atkTP
	 *            the atkTP to set
	 */
	public void setAtkTP(float atkTP) {
		this.atkTP = atkTP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.OBuffRound#stepRound(com.absir.aserv.game
	 * .value.OObject, long, int, com.absir.aserv.game.value.OResult)
	 */
	@Override
	public void stepRound(OCard object, long time, int round, IResult buffResult) {

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
		object.setAtk((int) object.getBuffAttPR("atk", object.baseAtk(), atkTPR));
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
		atkTPR *= atkTP;
		object.setAtk((int) object.getBuffAttP("atk", object.baseAtk(), atkTP));
	}
}
