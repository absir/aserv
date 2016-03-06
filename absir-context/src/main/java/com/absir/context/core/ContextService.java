/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-10 下午1:48:14
 */
package com.absir.context.core;

/**
 * @author absir
 * 
 */
public abstract class ContextService extends ContextBase {

	/**
	 * MUST NIO
	 * 
	 * @param contextTime
	 */
	public abstract void step(long contextTime);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.context.IContext#stepDone(long)
	 */
	@Override
	public final boolean stepDone(long contextTime) {
		step(contextTime);
		return false;
	}
}
