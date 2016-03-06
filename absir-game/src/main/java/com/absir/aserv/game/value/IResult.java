/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-5 上午9:44:46
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public interface IResult {

	/**
	 * @return
	 */
	public boolean isDone();

	/**
	 * @param done
	 */
	public void setDone(boolean done);

	/**
	 * @return
	 */
	public EResult getResult();

	/**
	 * @param result
	 */
	public void setResult(EResult result);
}
