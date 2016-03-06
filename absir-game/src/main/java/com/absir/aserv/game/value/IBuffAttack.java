/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 下午6:16:05
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public interface IBuffAttack<T> extends IBuffFrom<T> {

	/**
	 * @param atk
	 * @param damageFrom
	 * @param result
	 * @return
	 */
	public int attack(int atk, T damageFrom, IResult result);

}
