package com.absir.shared.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;

/**
 * Created by absir on 2016/11/18.
 */
@MappedSuperclass
public class SlaveStatus {

    @JaLang("升级状态")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private EUpgradeStatus upgradeStatus;

    @JaLang("变更时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dataTime")
    @JsonIgnore
    private long updateTime;

    @JaLang("进度")
    private long progress;

    @JaLang("失败")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean failed;

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

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

}
