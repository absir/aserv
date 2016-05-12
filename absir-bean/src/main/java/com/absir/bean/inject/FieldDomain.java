package com.absir.bean.inject;

import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Domain;
import com.absir.bean.inject.value.IDomain;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by absir on 16/3/23.
 */
@Base
@Bean
public class FieldDomain implements IFieldSupport {

    @Override
    public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Field field) {
        Domain domain = field.getAnnotation(Domain.class);
        return domain == null ? null : new InjectFieldDomain(field, domain);
    }

    public static class InjectFieldDomain extends InjectInvoker {

        private Field field;

        private Domain domain;

        public InjectFieldDomain(Field field, Domain domain) {
            this.field = field;
            this.domain = domain;
        }

        @Override
        public void invoke(BeanFactory beanFactory, Object beanObject) {
            try {
                if (field.get(beanObject) != null) {
                    return;
                }

            } catch (Exception e) {
                throw new RuntimeException("Can not get " + beanObject + '.' + field + " : ", e);
            }

            Class<?> beanType = field.getType();
            List<BeanDefine> beanDefines = beanFactory.getBeanDefines(field.getType());
            if (beanDefines.isEmpty()) {
                beanFactory.registerBeanDefine(new InjectBeanDefine(new BeanDefineType(beanType), BeanScope.PROTOTYPE));
            }

            Object domainObject = beanFactory.getBeanObject(field.getDeclaringClass().getSimpleName() + '.' + field.getName(), beanType, true);
            if (domainObject instanceof IDomain) {
                ((IDomain) domainObject).setDomain(domain.value());
            }

            try {
                field.set(beanObject, domainObject);

            } catch (Exception e) {
                throw new RuntimeException("Can not inject " + beanObject + '.' + field + " : " + domainObject, e);
            }
        }
    }
}
