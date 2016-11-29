package com.absir.shared.bean;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * Created by absir on 2016/11/18.
 */
public enum EUpgradeStatus {

    @JaLang("准备下载")READY_DOWNLOADING,

    @JaLang("下载完成")DOWNLOADING_COMPLETE,

    @JaLang("开始保存数据")BEGIN_SAVEDATA,

    @JaLang("开始重启升级")BEGIN_RESTART_UPRADE,

    @JaLang("刷新资源")REFRESH_RESOUCE_COMPLETE,

    @JaLang("启动升级完成")START_UPRADE_COMPLETE,
}
