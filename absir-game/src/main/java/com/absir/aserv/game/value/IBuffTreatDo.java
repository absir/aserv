/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-6 上午10:16:42
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface IBuffTreatDo<T, O extends OObject> extends IBuffFrom<T> {

	/**
	 * @param self
	 * @param target
	 * @param treat
	 * @param treatFrom
	 * @param result
	 */
	public void treatDo(O self, O target, int treat, T treatFrom, IResult result);

}
