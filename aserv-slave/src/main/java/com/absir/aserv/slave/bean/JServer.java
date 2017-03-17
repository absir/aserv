/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午4:17:01
 */
package com.absir.aserv.slave.bean;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.sockser.JbServerBase;

import javax.persistence.Entity;

@Entity
public class JServer extends JbServerBase {

    @JaLang("分组")
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
