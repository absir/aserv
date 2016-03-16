/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月16日 下午3:11:28
 */
package com.absir.validator;

public abstract class ValidatorValue implements Validator {

    @Override
    public final String validate(Object value) {
        return value == null ? null : validateValue(value);
    }

    public abstract String validateValue(Object value);
}
