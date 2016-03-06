/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-5 下午4:19:58
 */
package com.absir.aserv.game.value;

import com.absir.aserv.system.context.value.ObjectParameters;

/**
 * @author absir
 * 
 */
public class OEffect extends ObjectParameters {

	// 效果名称
	protected transient String effectName;

	/**
	 * @param parameters
	 */
	public OEffect(String[] parameters) {
		super(parameters);
		effectName = generateEffectName();
	}

	/**
	 * @return
	 */
	protected String generateEffectName() {
		return getClass().getSimpleName().substring(OEffect.class.getSimpleName().length() + 1);
	}

	/**
	 * @return the effectName
	 */
	public String getEffectName() {
		return effectName;
	}
}
