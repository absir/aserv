/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbServerTargetsOpen extends JbServerTargets {

    @JaLang("开启间隔")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openSubDay;

    @JaLang("开启持续")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openLifeDay;

    public int getOpenSubDay() {
        return openSubDay;
    }

    public void setOpenSubDay(int openSubDay) {
        this.openSubDay = openSubDay;
    }

    public int getOpenLifeDay() {
        return openLifeDay;
    }

    public void setOpenLifeDay(int openLifeDay) {
        this.openLifeDay = openLifeDay;
    }
}
