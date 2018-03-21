package com.absir.aserv.crud;

import com.absir.bean.lang.LangCodeUtils;
import com.absir.server.in.InModel;

import java.lang.reflect.TypeVariable;

public interface ICrudSubmit<T extends Enum> {

    public static final String MERGE_CODE = "@MERGE";

    public static final String AJAX_CODE = "@AJAX";

    public static final String OPTION_FAIL = LangCodeUtils.get("操作失败", ICrudSubmit.class);

    public static final String SEND_FAIL = LangCodeUtils.get("发送失败", ICrudSubmit.class);

    public static final TypeVariable TYPE_VARIABLE = ICrudSubmit.class.getTypeParameters()[0];

    public String submitOption(T option, InModel model) throws Throwable;

}
