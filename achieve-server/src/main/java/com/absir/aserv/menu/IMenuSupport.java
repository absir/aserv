/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-4 下午8:03:03
 */
package com.absir.aserv.menu;

import com.absir.aserv.system.bean.proxy.JiUserBase;

/**
 * @author absir
 */
public interface IMenuSupport {

    /**
     * @return
     */
    public String getMenuType();

    /**
     * @param menuBean
     * @param user
     * @return
     */
    public boolean isPermission(IMenuBean menuBean, JiUserBase user);
}
