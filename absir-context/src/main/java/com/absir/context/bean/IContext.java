/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-11 下午4:44:15
 */
package com.absir.context.bean;

/**
 * @author absir
 * 
 */
public interface IContext extends IStep {

	/**
	 * @param contextTime
	 */
	public void retainAt(long contextTime);

	/**
	 * MUST NIO
	 * 
	 * @return
	 */
	public boolean uninitializeDone();

	/**
	 * 
	 */
	public void uninitialize();
}
