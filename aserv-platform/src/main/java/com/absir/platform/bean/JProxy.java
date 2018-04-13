package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpen;

import javax.persistence.Entity;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "代理")
@Entity
public class JProxy extends JbBeanS implements JiOpen {

    @JaLang("转发地址")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String redirectAddress;

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("记录")
    @JaEdit(groups = JaEdit.GROUP_LIST, metas = "{\"option\":1}")
    private boolean record;

    public String getRedirectAddress() {
        return redirectAddress;
    }

    public void setRedirectAddress(String redirectAddress) {
        this.redirectAddress = redirectAddress;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }
}
