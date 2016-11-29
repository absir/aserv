/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月16日 上午10:39:12
 */
package com.absir.aserv.slave.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;

public class JSlaveUpgrade extends JbBase {

    @JaLang("主机")
    @Id
    private String id;

    @JaLang("升级状态")
    private EUpgradeStatus upgradeStatus;

    @JaLang("失败")
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
