/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-14 下午6:37:29
 */
package com.absir.context.core;

import com.absir.context.bean.IContext;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public class ContextWrapper extends ContextBase {

	/** context */
	private IContext context;

	/**
	 * 
	 */
	public ContextWrapper(IContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.context.IContext#retainAt(long)
	 */
	@Override
	public void retainAt(long contextTime) {
		context.retainAt(contextTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.context.IContext#stepDone(long)
	 */
	@Override
	public boolean stepDone(long contextTime) {
		return context.stepDone(contextTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.context.IContext#uninitialize()
	 */
	@Override
	public void uninitialize() {
		context.uninitialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == null || (obj instanceof ContextWrapper ? KernelObject.equals(context, ((ContextWrapper) obj).context) : KernelObject.equals(context, obj));
	}
}
