/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-29 下午1:08:10
 */
package com.absir.aserv.menu;

import com.absir.aserv.menu.value.MeUrlType;
import com.absir.core.kernel.KernelList.Orderable;

import java.util.Collection;

public interface IMenuBean extends Orderable {

    public String getName();

    public int getOrder();

    public String getUrl();

    public String getRef();

    public MeUrlType getUrlType();

    public String getIcon();

    public Collection<? extends IMenuBean> getChildren();

}
