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
import com.absir.core.kernel.KernelDyna;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Digits;

import java.util.Map;

@Bean
public class ValidatorNumber extends PropertyResolverAbstract<ValidatorObject, Digits> {

    public static final String NUMBER = LangCodeUtils.get("请输入合法的数字", ValidatorNumber.class);

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Digits annotation) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value, ILangMessage langMessage) {
                if (value != null && value instanceof CharSequence) {
                    CharSequence string = (CharSequence) value;
                    if (string.length() > 0 && KernelDyna.to(value, Double.class) == null) {
                        return langMessage == null ? "Number" : langMessage.getLangMessage(NUMBER);
                    }
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                return "number";
            }

        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectAnnotation(propertyObject, null);
    }
}
