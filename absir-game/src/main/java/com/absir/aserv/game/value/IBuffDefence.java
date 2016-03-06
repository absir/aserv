/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 下午4:46:23
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public interface IBuffDefence<T> extends IBuffFrom<T> {

	/**
	 * @param atk
	 * @param damageFrom
	 * @param result
	 * @return
	 */
	public int defence(int atk, T damageFrom, IResult result);

}
