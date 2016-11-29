package com.absir.master.bean;

import com.absir.aserv.master.bean.base.JbBeanServersO;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;

/**
 * Created by absir on 16/3/23.
 */
@MaEntity(parent = {@MaMenu("节点管理")}, name = "白名单", value = @MaMenu(order = -127))
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JWhitelist extends JbBeanServersO {

    @JaLang("白名单")
    @JaEdit(types = "text")
    private String whiteList;

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }
}
