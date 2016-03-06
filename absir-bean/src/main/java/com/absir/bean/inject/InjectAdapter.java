/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-7 下午12:17:51
 */
package com.absir.bean.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author absir
 * 
 */
public class InjectAdapter {

	/** Instance */
	private static InjectAdapter Instance;

	/** fields */
	Set<Field> fields = new HashSet<Field>();

	/** methods */
	Set<Method> methods = new HashSet<Method>();

	/**
	 * @return the instance
	 */
	public static InjectAdapter getInstance() {
		return Instance;
	}

	/**
	 * @param field
	 */
	public static void inject(Field field) {
		if (Instance == null) {
			Instance = new InjectAdapter();
		}

		if (Instance.fields.add(field)) {
			field.setAccessible(true);
		}
	}

	/**
	 * @param method
	 */
	public static void inject(Method method) {
		if (Instance == null) {
			Instance = new InjectAdapter();
		}

		if (Instance.methods.add(method)) {
			method.setAccessible(true);
		}
	}

	/**
	 * 
	 */
	protected static void clear() {
		if (Instance != null) {
			Instance.fields.clear();
			Instance.methods.clear();
		}

		Instance = null;
	}
}
