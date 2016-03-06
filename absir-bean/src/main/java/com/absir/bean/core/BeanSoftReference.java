/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-13 下午2:48:58
 */
package com.absir.bean.core;

import java.lang.ref.SoftReference;

import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public final class BeanSoftReference extends SoftReference<Object> {

	/**
	 * @param bean
	 */
	protected BeanSoftReference(Object bean) {
		super(bean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o != null) {
			if (o instanceof BeanSoftReference) {
				return KernelObject.equals(get(), ((BeanSoftReference) o).get());
			}

			return o.equals(get());
		}

		return false;
	}
}
