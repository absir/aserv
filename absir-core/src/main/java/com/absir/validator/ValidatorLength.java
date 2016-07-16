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
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Length;

import java.text.MessageFormat;
import java.util.Map;

@Bean
public class ValidatorLength extends PropertyResolverAbstract<ValidatorObject, Length> {

    public static final String LENGTH = LangCodeUtils.get("请输入长度在 {0} 到 {1} 之间的字符串", ValidatorLength.class);

    public ValidatorObject getPropertyObjectLength(ValidatorObject propertyObject, final int min, final int max) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new Validator() {

            @Override
            public String validate(Object value, ILangMessage langMessage) {
                if (value != null && value instanceof CharSequence) {
                    CharSequence string = (CharSequence) value;
                    if (string.length() > 0) {
                        int length = string.length();
                        if (length >= 0 && (length < min || length > max)) {
                            return langMessage == null ? (min + " - " + max + " Length") : MessageFormat.format(langMessage.getLangMessage(LENGTH), min, max);
                        }
                    }
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                if (min > 0) {
                    validatorMap.put("minlength", min);
                }

                if (max < Integer.MAX_VALUE) {
                    validatorMap.put("maxlength", max);
                }

                return null;
            }
        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Length annotation) {
        return getPropertyObjectLength(propertyObject, annotation.min(), annotation.max());
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        String[] parameters = annotationValue.split(",");
        if (parameters.length == 2) {
            return getPropertyObjectLength(propertyObject, DynaBinder.to(parameters[0], int.class), DynaBinder.to(parameters[1], int.class));
        }

        return propertyObject;
    }
}
