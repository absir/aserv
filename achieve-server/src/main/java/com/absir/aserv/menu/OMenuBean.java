/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-29 下午5:13:24
 */
package com.absir.aserv.menu;

import com.absir.aserv.menu.value.MeUrlType;

import java.util.Collection;
import java.util.List;

public class OMenuBean implements IMenuBean {

    private IMenuBean menuBean;

    private String url;

    private List<? extends OMenuBean> children;

    public OMenuBean(IMenuBean menuBean) {
        this.menuBean = menuBean;
    }

    @Override
    public String getName() {
        return menuBean.getName();
    }

    @Override
    public int getOrder() {
        return menuBean.getOrder();
    }

    @Override
    public String getUrl() {
        if (url == null) {
            url = MenuContextUtils.getUrl(getBaseUrl(), getUrlType());
        }

        return url;
    }

    @Override
    public String getRef() {
        return menuBean.getRef();
    }

    @Override
    public MeUrlType getUrlType() {
        return menuBean.getUrlType();
    }

    @Override
    public Collection<? extends OMenuBean> getChildren() {
        return children;
    }

    public void setChildren(List<? extends OMenuBean> children) {
        this.children = children;
    }

    public String getBaseUrl() {
        return menuBean.getUrl();
    }

    @Override
    public String getIcon() {
        return menuBean.getIcon();
    }
}
