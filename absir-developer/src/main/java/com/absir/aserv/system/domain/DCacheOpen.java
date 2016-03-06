/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月19日 上午10:17:04
 */
package com.absir.aserv.system.domain;

import com.absir.aserv.system.bean.value.JiOpenValue;

/**
 * @author absir
 *
 */
public class DCacheOpen<V, K extends JiOpenValue<V>> extends DCache<K, V> {

	/**
	 * @param entityClass
	 * @param entityName
	 */
	public DCacheOpen(Class<K> entityClass, String entityName) {
		super(entityClass, entityName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.domain.DCache#getCacheValue(com.absir.aserv.
	 * system.bean.proxy.JiBase)
	 */
	@Override
	protected V getCacheValue(K entity) {
		if (!entity.isOpen()) {
			return null;
		}

		return entity.getValue();
	}

}
