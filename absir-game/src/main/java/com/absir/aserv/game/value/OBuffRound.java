/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-17 下午7:45:18
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class OBuffRound<T extends OObject> extends OBuffReverse<T> {

	// 回合数
	private int round;

	/**
	 * @return the round
	 */
	public int getRound() {
		return round;
	}

	/**
	 * @param round
	 *            the round to set
	 */
	public void setRound(int round) {
		this.round = round;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.OBuff#step(com.absir.aserv.game.value.
	 * OObject, long, com.absir.aserv.game.value.OResult)
	 */
	@Override
	public final void step(T self, long time, IResult result) {
		if (round > 0) {
			if (--round == 0) {
				result.setDone(true);
			}
		}

		stepRound(self, time, round, result);
	}

	/**
	 * @param self
	 * @param time
	 * @param round
	 * @param result
	 */
	public abstract void stepRound(T self, long time, int round, IResult result);
}
