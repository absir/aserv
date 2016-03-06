/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-15 下午1:20:31
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public class LevelExpCxt<T extends ILevelExp> extends LevelCxt<T> {

	/**
	 * @param obj
	 * @return
	 */
	public int getExp(T obj) {
		return obj.getExp();
	}

	/**
	 * @param obj
	 * @param exp
	 */
	public void setExp(T obj, int exp) {
		obj.setExp(exp);
	}
}
