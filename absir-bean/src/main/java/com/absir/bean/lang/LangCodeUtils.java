package com.absir.bean.lang;

import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;

/**
 * Created by absir on 16/7/16.
 */
@Configure
public class LangCodeUtils {

    public static final ILangCode LANG_CODE = BeanFactoryUtils.get(ILangCode.class);

    public static final ILangMessage LANG_MESSAGE = BeanFactoryUtils.get(ILangMessage.class);

    public static String get(String lang, Class<?> cls) {
        if (LANG_CODE != null) {
            String code = LANG_CODE.getLandCode(lang, cls);
            if (code != null) {
                return code;
            }
        }

        return lang;
    }

    public static String getLangMessage(String langCode) {
        return LANG_MESSAGE == null ? langCode : LANG_MESSAGE.getLangMessage(langCode);
    }

    public static String getLangMessage(String langCode, ILangMessage langMessage) {
        return langMessage == null ? langCode : langMessage.getLangMessage(langCode);
    }

}
