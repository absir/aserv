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
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "升级状态")
@Entity
public class JSlaveUpgrading extends JbBase {

    @JaLang("主机")
    @Id
    private String id;

    @JaLang("升级状态")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private EUpgradeStatus upgradeStatus;

    @JaLang("变更时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dataTime")
    @JsonIgnore
    private long updateTime;

    @JaLang("失败")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean failed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EUpgradeStatus getUpgradeStatus() {
        return upgradeStatus;
    }

    public void setUpgradeStatus(EUpgradeStatus upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public static enum EUpgradeStatus {

        @JaLang("准备下载")READY_DOWNLOADING,

        @JaLang("下载完成")DOWNLOADING_COMPLETE,

        @JaLang("开始保存数据")BEGIN_SAVEDATA,

        @JaLang("开始重启升级")BEGIN_RESTART_UPRADE,

        @JaLang("刷新资源")REFRESH_RESOUCE_COMPLETE,

        @JaLang("启动升级完成")START_UPRADE_COMPLETE,
    }

}
