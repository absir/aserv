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

/**
 * @author absir
 *
 */
@MappedSuperclass
public abstract class JbPlayerA extends JbBase {

    @JaLang("编号ID")
    @Id
    private Long id;

    @JaLang("游戏天数")
    @JaEdit(editable = JeEditable.DISABLE)
    private int gameDay;

    @JaLang("在线天数")
    @JaEdit(types = "dateTime")
    private int onlineDay;

    @JaLang("在线时间")
    @JaEdit(types = "dateTime")
    private long onlineTime;

    @JaLang("恢复时间")
    private long[] recoveryTimes;

    @JaLang("最后下线")
    private long lastOffline;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the gameDay
     */
    public int getGameDay() {
        return gameDay;
    }

    /**
     * @param gameDay
     *            the gameDay to set
     */
    public void setGameDay(int gameDay) {
        this.gameDay = gameDay;
    }

    /**
     * @return the onlineDay
     */
    public int getOnlineDay() {
        return onlineDay;
    }

    /**
     * @param onlineDay
     *            the onlineDay to set
     */
    public void setOnlineDay(int onlineDay) {
        this.onlineDay = onlineDay;
    }

    /**
     * @return the onlineTime
     */
    public long getOnlineTime() {
        return onlineTime;
    }

    /**
     * @param onlineTime
     *            the onlineTime to set
     */
    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    /**
     * @return the recoveryTimes
     */
    public long[] getRecoveryTimes() {
        return recoveryTimes;
    }

    /**
     * @param recoveryTimes
     *            the recoveryTimes to set
     */
    public void setRecoveryTimes(long[] recoveryTimes) {
        this.recoveryTimes = recoveryTimes;
    }

    /**
     * @return the lastOffline
     */
    public long getLastOffline() {
        return lastOffline;
    }

    /**
     * @param lastOffline
     *            the lastOffline to set
     */
    public void setLastOffline(long lastOffline) {
        this.lastOffline = lastOffline;
    }

}
