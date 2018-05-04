/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.context.schedule.validator;

import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.context.schedule.cron.CronSequenceGenerator;
import com.absir.context.schedule.value.Cron;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.ValidatorObject;
import com.absir.validator.ValidatorValue;

import java.util.Map;

@Bean
public class ValidatorCron extends PropertyResolverAbstract<ValidatorObject, Cron> {

    public static final String CRON = LangCodeUtils.get("请输入有效的Cron表达式", ValidatorCron.class);

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Cron annotation) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value, ILangMessage langMessage) {
                if (value != null && value instanceof String) {
                    CharSequence string = (CharSequence) value;
                    if (string.length() > 0) {
                        try {
                            new CronSequenceGenerator((String) string, null).getClass();

                        } catch (Exception e) {
                            return langMessage == null ? "Cron" : langMessage.getLangMessage(CRON);
                        }
                    }
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                return "cron";
            }
        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectAnnotation(propertyObject, null);
    }
}
