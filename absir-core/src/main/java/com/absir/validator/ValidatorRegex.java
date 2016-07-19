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
import com.absir.validator.value.Regex;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;

@Bean
public class ValidatorRegex extends PropertyResolverAbstract<ValidatorObject, Regex> {

    public static final String REGEX = LangCodeUtils.get("请输入正确的格式", ValidatorRegex.class);

    public ValidatorObject getPropertyObjectPattern(ValidatorObject propertyObject, final String regex, String lang) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        String langCode = null;
        if (!KernelString.isEmpty(lang)) {
            langCode = LangCodeUtils.get(lang, ValidatorRegex.class);
        }

        final String caption = langCode;
        final Pattern pattern = java.util.regex.Pattern.compile(regex);
        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value, ILangMessage langMessage) {
                if (value != null && value instanceof CharSequence) {
                    CharSequence string = (CharSequence) value;
                    if (string.length() > 0 && !pattern.matcher((CharSequence) value).matches()) {
                        return langMessage == null ? (regex + " Regex") : MessageFormat.format(langMessage.getLangMessage(caption == null ? REGEX : caption), regex);
                    }
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                validatorMap.put("pattern", regex);
                validatorMap.put("error", caption == null ? REGEX : caption);
                return null;
            }
        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Regex annotation) {
        return getPropertyObjectPattern(propertyObject, annotation.value(), annotation.lang());
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectPattern(propertyObject, annotationValue, null);
    }
}
