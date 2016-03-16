/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午3:59:58
 */
package com.absir.validator;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertySupply;
import com.absir.validator.value.ValidatorIngore;

import java.lang.annotation.Annotation;
import java.util.List;

@Base
@Bean
public class ValidatorSupply extends PropertySupply<ValidatorObject, List<Validator>> {

    @Override
    public Class<? extends Annotation> getIngoreAnnotationClass() {
        return ValidatorIngore.class;
    }

}
