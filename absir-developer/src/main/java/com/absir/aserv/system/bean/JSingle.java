package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;

/**
 * Created by absir on 16/5/30.
 */
@Entity
public class JSingle extends JbBeanS {

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }
}
