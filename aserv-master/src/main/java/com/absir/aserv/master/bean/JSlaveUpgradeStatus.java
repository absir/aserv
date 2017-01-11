/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月16日 上午10:39:12
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.proxy.JiBase;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.shared.bean.SlaveUpgradeStatus;

import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "状态")
@Entity
public class JSlaveUpgradeStatus extends SlaveUpgradeStatus implements JiBase {

    @JaLang(value = "节点编号", tag = "slaveId")
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
