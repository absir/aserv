/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月20日 上午11:39:03
 */
package com.absir.aserv.game.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class JbPlayerA extends JbBase {

    @JaLang("编号ID")
    @Id
    private Long id;

    @JaLang("游戏天数")
    @JaEdit(editable = JeEditable.DISABLE)
    private int gameDay;

    @JaLang("在线天数")
    private int onlineDay;

    @JaLang("在线时间")
    @JaEdit(types = "dateTime")
    private long onlineTime;

    @JaLang("恢复时间")
    private long[] recoveryTimes;

    @JaLang("最后下线")
    private long lastOffline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGameDay() {
        return gameDay;
    }

    public void setGameDay(int gameDay) {
        this.gameDay = gameDay;
    }

    public int getOnlineDay() {
        return onlineDay;
    }

    public void setOnlineDay(int onlineDay) {
        this.onlineDay = onlineDay;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long[] getRecoveryTimes() {
        return recoveryTimes;
    }

    public void setRecoveryTimes(long[] recoveryTimes) {
        this.recoveryTimes = recoveryTimes;
    }

    public long getLastOffline() {
        return lastOffline;
    }

    public void setLastOffline(long lastOffline) {
        this.lastOffline = lastOffline;
    }

}
