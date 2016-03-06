/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-6 上午10:15:08
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public interface IBuffTreatBoost<T> extends IBuffFrom<T> {

	/**
	 * @param hp
	 * @param treatFrom
	 * @param result
	 * @return
	 */
	public int boost(int hp, T treatFrom, IResult result);

}
