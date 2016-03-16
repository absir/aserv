/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-4 下午2:06:23
 */
package com.absir.aserv.system.bean.proxy;

import com.absir.aserv.system.bean.base.JbUserRole;

import java.util.Collection;

public interface JiUserBase {

    public Long getUserId();

    public boolean isDeveloper();

    public boolean isActivation();

    public boolean isDisabled();

    public String getUsername();

    public int getUserRoleLevel();

    public Collection<? extends JiUserRole> userRoles();

    public Collection<? extends JbUserRole> getUserRoles();

    public Object getMetaMap(String key);

    public void setMetaMap(String key, String value);
}
