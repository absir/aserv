/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-18 上午9:40:49
 */
package com.absir.bean.inject;

import java.util.ArrayList;
import java.util.List;

import com.absir.bean.inject.value.InjectType;

/**
 * @author absir
 * 
 */
public class InjectObserverClass {

	/** injectObservers */
	List<InjectObserver> injectObservers;

	/**
	 * @param injectInvokerObserver
	 */
	public void addInjectInvoker(InjectInvokerObserver injectInvokerObserver) {
		if (injectInvokerObserver.injectType == InjectType.ObServed || injectInvokerObserver.injectType == InjectType.ObServeRealed) {
			InjectObserver injectObserver = injectInvokerObserver.getInjectObserver();
			if (injectObserver != null) {
				if (injectObservers == null) {
					injectObservers = new ArrayList<InjectObserver>();
				}

				injectObservers.add(injectObserver);
			}
		}
	}
}
