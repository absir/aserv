/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-31 下午4:59:14
 */
package com.absir.aserv.system.security;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.server.in.Input;

public interface ISecurityService {

    public JiUserBase getUserBase(Long userId);

    public JiUserBase getUserBase(String username, int roleLevel);

    public boolean validator(JiUserBase userBase, String password, int error, long errorTime, String address);

    public JiUserBase openUserBase(String username, String password, String platform, String address);

    public SecurityContext autoLogin(String name, boolean remeber, int roleLevel, Input input);

    public SecurityContext login(String username, String password, long remember, int roleLevel, String name, Input input);

    public void logout(String name, Input input);
}
