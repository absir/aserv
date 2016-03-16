/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:06:39
 */
package com.absir.validator;

import java.util.Map;

public interface Validator {

    public String validate(Object value);

    public String getValidateClass(Map<String, Object> validatorMap);

}
