/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-21 上午10:59:40
 */
package com.absir.aserv.game.value;

import com.absir.aserv.system.context.value.ObjectParameters;

@SuppressWarnings("rawtypes")
public abstract class OTrigger<T extends OObject> extends ObjectParameters {

    public OTrigger(String[] parameters) {
        super(parameters);
    }

    public abstract boolean isTrigger(T self);

}
