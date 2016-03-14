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

/**
 * @author absir
 *
 */
public class JSlaveUpgrading extends JbBase {

    /**
     * id
     */
    @JaLang("主机")
    @Id
    private String id;

    @JaLang("升级状态")
    private EUpgradeStatus upgradeStatus;

    @JaLang("失败")
    private boolean failed;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the upgradeStatus
     */
    public EUpgradeStatus getUpgradeStatus() {
        return upgradeStatus;
    }

    /**
     * @param upgradeStatus
     *            the upgradeStatus to set
     */
    public void setUpgradeStatus(EUpgradeStatus upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }

    /**
     * @return the failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * @param failed
     *            the failed to set
     */
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /**
     * @author absir
     *
     */
    public static enum EUpgradeStatus {

        @JaLang("准备下载")READY_DOWNLOADING,

        @JaLang("下载完成")DOWNLOADING_COMPLETE,

        @JaLang("开始保存数据")BEGIN_SAVEDATA,

        @JaLang("开始重启升级")BEGIN_RESTART_UPRADE,

        @JaLang("刷新资源")REFRESH_RESOUCE_COMPLETE,

        @JaLang("启动升级完成")START_UPRADE_COMPLETE,
    }

}
