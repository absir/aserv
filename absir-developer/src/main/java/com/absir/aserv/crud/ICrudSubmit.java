package com.absir.aserv.crud;

import com.absir.bean.lang.LangCodeUtils;
import com.absir.server.in.InModel;

public interface ICrudSubmit<T extends Enum> {

    public static final String OPTION_FAIL = LangCodeUtils.get("操作失败", ICrudSubmit.class);

    public static final String SEND_FAIL = LangCodeUtils.get("发送失败", ICrudSubmit.class);

    public Class<T> classForOption();

    public String submitOption(T option, InModel model);

}
