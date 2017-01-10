package com.absir.shared.bean;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * Created by absir on 2016/11/18.
 */
public enum EUpgradeStatus {

    @JaLang("下载...")DOWNLOADING,

    @JaLang("下载完成")DOWNLOAD_COMPLETE,

    @JaLang("文件验证")FILE_VALIDATE,

    @JaLang("资源准备")RESOURCE_READY,

    @JaLang("资源完成")RESOURCE_COMPLETE,

    @JaLang("重启准备")RESTART_READY,

    @JaLang("重启开始")RESTART_BEGIN,

    @JaLang("重启完成")RESTART_COMPLETE,

}
