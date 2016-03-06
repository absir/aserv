/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-8 下午3:59:58
 */
package com.absir.validator;

import java.lang.annotation.Annotation;
import java.util.List;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertySupply;
import com.absir.validator.value.ValidatorIngore;

/**
 * @author absir
 * 
 */
@Base
@Bean
public class ValidatorSupply extends PropertySupply<ValidatorObject, List<Validator>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertySupply#getIngoreAnnotationClass()
	 */
	@Override
	public Class<? extends Annotation> getIngoreAnnotationClass() {
		return ValidatorIngore.class;
	}

}
