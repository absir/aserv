/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.proxy.JiUser;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbPermission extends JbAssoc implements JiUser {

    @JaLang("关联用户")
    private Long userId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
