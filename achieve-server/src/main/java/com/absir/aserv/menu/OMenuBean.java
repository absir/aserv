/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-29 下午5:13:24
 */
package com.absir.aserv.menu;

import java.util.Collection;
import java.util.List;

import com.absir.aserv.menu.value.MeUrlType;

/**
 * @author absir
 * 
 */
public class OMenuBean implements IMenuBean {

	/** menuBean */
	private IMenuBean menuBean;

	/** url */
	private String url;

	/** children */
	private List<? extends OMenuBean> children;

	/**
	 * @param menuBean
	 */
	public OMenuBean(IMenuBean menuBean) {
		this.menuBean = menuBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getName()
	 */
	@Override
	public String getName() {
		return menuBean.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getOrder()
	 */
	@Override
	public int getOrder() {
		return menuBean.getOrder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getUrl()
	 */
	@Override
	public String getUrl() {
		if (url == null) {
			url = MenuContextUtils.getUrl(getBaseUrl(), getUrlType());
		}

		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getRef()
	 */
	@Override
	public String getRef() {
		return menuBean.getRef();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getUrlType()
	 */
	@Override
	public MeUrlType getUrlType() {
		return menuBean.getUrlType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getChildren()
	 */
	@Override
	public Collection<? extends OMenuBean> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<? extends OMenuBean> children) {
		this.children = children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getUrl()
	 */
	public String getBaseUrl() {
		return menuBean.getUrl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.feature.menu.IMenuBean#getIcon()
	 */
	@Override
	public String getIcon() {
		return menuBean.getIcon();
	}
}
