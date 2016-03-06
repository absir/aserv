/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月6日 上午10:00:09
 */
package com.absir.data.base;

import com.absir.data.value.IDirty;

/**
 * @author absir
 *
 */
public class DDirty implements IDirty {

	/** dirty */
	protected transient boolean dirty;

	/**
	 * @param index
	 */
	public void setDirtyI(int index) {
		dirty = true;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.data.value.IDirty#setDirty()
	 */
	@Override
	public void setDirty() {
		dirty = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.data.value.IDirty#clearDirty()
	 */
	@Override
	public void clearDirty() {
		dirty = false;
	}

}
