/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月11日 下午3:34:37
 */
package com.absir.spring;

import com.absir.bean.basis.*;
import com.absir.bean.config.IBeanDefineProcessor;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Stopping;
import com.absir.spring.annotation.ASpring;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

/**
 * @author absir
 */
@Basis
@Bean
public class SpringBeanDefineAware implements IBeanFactoryAware, IBeanDefineProcessor {

    /**
     * context
     */
    private static ClassPathXmlApplicationContext context;

    /**
     * @return
     */
    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

    /*
         * (non-Javadoc)
         *
         * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
         */
    @Override
    public int getOrder() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.config.IBeanFactoryAware#beforeRegister(com.absir.bean
     * .core.BeanFactoryImpl)
     */
    @Override
    public void beforeRegister(BeanFactoryImpl beanFactory) {
        if (context != null) {
            return;
        }

        final BeanConfig beanConfig = beanFactory.getBeanConfig();
        String[] locations = beanConfig.getExpressionObject("spring.locations", null, String[].class);
        if (locations == null) {
            locations = new String[]{"classpath:/spring/*.xml"};
        }

        context = new ClassPathXmlApplicationContext(locations, false);
        beanFactory.addBeanSupply(new BeanSupply() {

            @Override
            public <T> Collection<T> getBeanObjects(Class<T> beanType) {
                try {
                    return context.getBeansOfType(beanType).values();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public <T> T getBeanObject(String beanName, Class<T> beanType) {
                try {
                    return context.getBean(beanName, beanType);

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public <T> T getBeanObject(Class<T> beanType) {
                try {
                    return context.getBean(beanType);

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public Object getBeanObject(String beanName) {
                try {
                    return context.getBean(beanName);

                } catch (Exception e) {
                    return null;
                }
            }
        });

        context.refresh();
        context.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.config.IBeanFactoryAware#afterRegister(com.absir.bean.
     * core.BeanFactoryImpl)
     */
    @Override
    public void afterRegister(BeanFactoryImpl beanFactory) {
    }

    /**
     *
     */
    @Stopping
    protected void stopping() {
        if (context != null) {
            context.stop();
            context.close();
            context = null;
        }
    }

    @Override
    public BeanDefine getBeanDefine(BeanFactory beanFactory, BeanDefine beanDefine) {
        if (beanDefine.getBeanType().getAnnotation(ASpring.class) != null) {
            beanDefine = new SpringBeanDefineWrapper(beanDefine);
        }

        return beanDefine;
    }
}
