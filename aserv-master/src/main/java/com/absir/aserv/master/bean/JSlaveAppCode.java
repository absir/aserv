package com.absir.aserv.master.bean;

import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpen;

import javax.persistence.Entity;

/**
 * Created by absir on 2017/1/9.
 */
@Entity
public class JSlaveAppCode extends JbBeanS implements JiOpen {

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}
