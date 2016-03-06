/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月24日 上午11:29:27
 */
package com.absir.context.bean;

/**
 * @author absir
 *
 */
public interface IStep {

	/**
	 * @param contextTime
	 * @return
	 */
	public boolean stepDone(long contextTime);

}
