/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-14 下午1:31:09
 */
package com.absir.core.util;

import java.io.Serializable;
import java.util.Map;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UtilAbsir {

	/** DAY_TIME */
	public static final long DAY_TIME = 24 * 3600000;

	/** WEEK_TIME */
	public static final long WEEK_TIME = 7 * DAY_TIME;

	/**
	 * @param cls
	 * @param id
	 * @return
	 */
	public static String getId(Class<?> cls, Serializable id) {
		return cls.getName() + '@' + id;
	}

	/**
	 * @param id
	 * @param tokenMap
	 */
	public static Object getToken(Object id, Map<?, ?> tokenMap) {
		Object token = tokenMap.get(id);
		if (token == null) {
			synchronized (tokenMap) {
				token = tokenMap.get(id);
				if (token == null) {
					token = new Object();
					((Map) tokenMap).put(id, token);
				}
			}
		}

		return token;
	}

	/**
	 * @param id
	 * @param tokenMap
	 * @return
	 */
	public static Object clearToken(Object id, Map<?, ?> tokenMap) {
		synchronized (tokenMap) {
			return tokenMap.remove(id);
		}
	}

	/**
	 * @param cls
	 * @param id
	 * @param tokenMap
	 * @return
	 */
	public static Object getToken(Class<?> cls, Serializable id, Map<?, ?> tokenMap) {
		return getToken(getId(cls, id), tokenMap);
	}
}
