/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年8月26日 下午2:42:42
 */
package com.absir.aserv.lang;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JeEditable;

public interface ILangBase {

    public <T> T getLang(String fieldName, Integer locale, Class<T> type);

    public void setLang(String fieldName, Integer locale, Object value);

    @JaEdit(editable = JeEditable.DISABLE)
    public void setLangValues(String[] values);

}
