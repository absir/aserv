/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.validator;

import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Confirm;

import java.util.Map;

@Bean
public class ValidatorConfirm extends PropertyResolverAbstract<ValidatorObject, Confirm> {

    public static final String CONFIRM = LangCodeUtils.get("内容不一致", ValidatorConfirm.class);

    public ValidatorObject getPropertyObjectPattern(ValidatorObject propertyObject, final String confirm, String lang) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        String langCode = null;
        if (!KernelString.isEmpty(lang)) {
            langCode = LangCodeUtils.get(lang, ValidatorConfirm.class);
        }

        final String caption = langCode;
        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value, ILangMessage langMessage) {
                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                validatorMap.put("confirm", confirm);
                validatorMap.put("error", caption == null ? CONFIRM : caption);
                return null;
            }
        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Confirm annotation) {
        return getPropertyObjectPattern(propertyObject, annotation.value(), annotation.lang());
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectPattern(propertyObject, annotationValue, null);
    }
}
