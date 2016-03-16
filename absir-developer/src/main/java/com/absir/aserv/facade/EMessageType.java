package com.absir.aserv.facade;

import com.absir.aserv.system.bean.value.JaLang;

public enum EMessageType {

    @JaLang("描述")
    INFO,

    @JaLang("成功")
    SUCCESS,

    @JaLang("警告")
    WARN,

    @JaLang("错误")
    ERROR,
}
