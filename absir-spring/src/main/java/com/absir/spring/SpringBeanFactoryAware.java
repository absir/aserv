package com.absir.spring;

import com.absir.bean.core.BeanFactoryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.StringValueResolver;

public class SpringBeanFactoryAware implements BeanFactoryAware {

    private static boolean awared = false;

    private StringValueResolver valueResolver;

    public StringValueResolver getValueResolver() {
        if (valueResolver == null) {
            valueResolver = new StringValueResolver() {
                @Override
                public String resolveStringValue(String s) {
                    return BeanFactoryUtils.getBeanConfig().getExpression(s, true);
                }
            };
        }

        return valueResolver;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (awared) {
            return;
        }

        if (beanFactory instanceof ConfigurableBeanFactory) {
            awared = true;
            ((ConfigurableBeanFactory) beanFactory).resolveAliases(getValueResolver());
        }
    }
}
