/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午4:19:58
 */
package com.absir.aserv.game.value;

import com.absir.aserv.system.context.value.ObjectParameters;

public class OEffect extends ObjectParameters {

    // 效果名称
    protected transient String effectName;

    public OEffect(String[] parameters) {
        super(parameters);
        effectName = generateEffectName();
    }

    protected String generateEffectName() {
        return getClass().getSimpleName().substring(OEffect.class.getSimpleName().length() + 1);
    }

    public String getEffectName() {
        return effectName;
    }
}
