/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年9月11日 下午1:54:39
 */
package com.absir.open.service;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;

/**
 * @author absir
 */
@Inject
public interface IPlatformIdentity {

    /**
     * PLATFORM_IDENTITY
     */
    public static final IPlatformIdentity PLATFORM_IDENTITY = BeanFactoryUtils.get(IPlatformIdentity.class);

    /**
     * @param userBase
     * @return
     */
    public String loginPlatform(JiUserBase userBase);

}
