/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-6 上午10:17:28
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface IBuffTreatReflect<T, O extends OObject> extends IBuffFrom<T> {

	/**
	 * @param self
	 * @param target
	 * @param treat
	 * @param treatFrom
	 * @param result
	 */
	public void treatReflect(O self, O target, int treat, T treatFrom, IResult result);

}
