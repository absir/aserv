/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-29 下午1:08:10
 */
package com.absir.aserv.menu;

import java.util.Collection;

import com.absir.aserv.menu.value.MeUrlType;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IMenuBean extends Orderable {

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public int getOrder();

	/**
	 * @return
	 */
	public String getUrl();

	/**
	 * @return
	 */
	public String getRef();

	/**
	 * @return
	 */
	public MeUrlType getUrlType();
	
	/**
	 * @return
	 */
	public String getIcon();

	/**
	 * @return
	 */
	public Collection<? extends IMenuBean> getChildren();

}
