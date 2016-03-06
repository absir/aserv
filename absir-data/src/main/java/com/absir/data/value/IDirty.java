/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月6日 上午10:02:25
 */
package com.absir.data.value;

/**
 * @author absir
 *
 */
public interface IDirty {

	/**
	 * @return
	 */
	public boolean isDirty();

	/**
	 * 
	 */
	public void setDirty();

	/**
	 * 
	 */
	public void clearDirty();

}
