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
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Confirm;

import java.util.Map;

@Bean
public class ValidatorConfirm extends PropertyResolverAbstract<ValidatorObject, Confirm> implements KernelList.Orderable {

    public static final String CONFIRM = LangCodeUtils.get("内容不一致", ValidatorConfirm.class);

    public static final Validator CONFIRM_VALIDATE = new Validator() {
        @Override
        public String validate(Object value, ILangMessage langMessage) {
            return null;
        }

        @Override
        public String getValidateClass(Map<String, Object> validatorMap) {
            return null;
        }
    };

    public ValidatorObject getPropertyObjectPattern(ValidatorObject propertyObject, final String confirm, String lang, boolean client) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        String langCode = null;
        if (!KernelString.isEmpty(lang)) {
            langCode = LangCodeUtils.get(lang, ValidatorConfirm.class);
        }

        final String caption = langCode;
        if (client) {
            propertyObject.addValidator(CONFIRM_VALIDATE);
        }

        propertyObject.addValidator(new Validator() {

            @Override
            public String validate(Object value, ILangMessage langMessage) {
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
        return getPropertyObjectPattern(propertyObject, annotation.value(), annotation.lang(), annotation.client());
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        String[] parameters = annotationValue.split(",");
        return getPropertyObjectPattern(propertyObject, parameters[0], parameters.length > 1 ? parameters[1] : null, parameters.length > 2 ? true : KernelDyna.toBoolean(parameters[2], Boolean.TRUE));
    }

    @Override
    public int getOrder() {
        return -64;
    }
}
