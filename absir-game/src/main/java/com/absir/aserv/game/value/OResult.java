/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-6 下午2:01:54
 */
package com.absir.aserv.game.value;

/**
 * @author absir
 * 
 */
public class OResult implements IResult {

	/** done */
	private boolean done;

	/** result */
	private EResult result;

	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @param done
	 *            the done to set
	 */
	public void setDone(boolean done) {
		this.done = done;
	}

	/**
	 * @return the result
	 */
	public EResult getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(EResult result) {
		this.result = result;
	}
}
