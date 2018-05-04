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
import com.absir.aserv.system.bean.value.JiOpen;
import com.absir.aserv.system.bean.value.JiOrdinal;
import com.absir.context.schedule.value.Cron;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbServerTargetsCron extends JbServerTargets implements JiOpen, JiOrdinal {

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("排序")
    private int ordinal;

    @Cron
    @JaLang("开启周期")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String openCron;

    @JaLang("开启间隔")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openSubDay;

    @JaLang("开启持续")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openLifeDay;

    @JaLang("最后开启")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long openLastTime;

    @JaLang("独立开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean openSingly;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getOpenCron() {
        return openCron;
    }

    public void setOpenCron(String openCron) {
        this.openCron = openCron;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

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

    public long getOpenLastTime() {
        return openLastTime;
    }

    public void setOpenLastTime(long openLastTime) {
        this.openLastTime = openLastTime;
    }

    public boolean isOpenSingly() {
        return openSingly;
    }

    public void setOpenSingly(boolean openSingly) {
        this.openSingly = openSingly;
    }

}
