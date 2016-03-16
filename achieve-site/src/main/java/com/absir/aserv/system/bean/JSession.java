/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-11 下午1:03:49
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;

@MaEntity(parent = {@MaMenu("登录管理")}, name = "登录")
@Entity
public class JSession extends JbSession {

    @JaLang("用户参数")
    private String userParameter;

    public String getUserParameter() {
        return userParameter;
    }

    public void setUserParameter(String userParameter) {
        this.userParameter = userParameter;
    }
}
