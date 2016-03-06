/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 下午4:49:30
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface IBuffReBound<T, O extends OObject> extends IBuffFrom<T> {

	/**
	 * @param self
	 * @param target
	 * @param damage
	 * @param damageFrom
	 * @param result
	 * @return
	 */
	public int reBound(O self, O target, int damage, T damageFrom, IResult result);

}
