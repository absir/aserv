/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月16日 下午3:11:28
 */
package com.absir.validator;

import com.absir.bean.lang.ILangMessage;

public abstract class ValidatorValue implements Validator {

    @Override
    public final String validate(Object value, ILangMessage langMessage) {
        return value == null ? null : validateValue(value, langMessage);
    }

    public abstract String validateValue(Object value, ILangMessage langMessage);
}
