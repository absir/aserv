/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-18 上午9:40:49
 */
package com.absir.bean.inject;

import com.absir.bean.inject.value.InjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 */
public class InjectObserverClass {

    /**
     * injectObservers
     */
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
