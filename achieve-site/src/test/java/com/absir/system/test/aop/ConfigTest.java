/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月11日 上午10:12:34
 */
package com.absir.system.test.aop;

import com.absir.aserv.advice.MethodAfter;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelObject;
import com.absir.system.test.AbstractTestInject;
import org.junit.Test;

import java.lang.reflect.Method;

public class ConfigTest extends AbstractTestInject {

    @Test
    public void test() throws Throwable {
        AdviceBean.ME.peek("TTT");
    }

    @Bean
    public static class AdviceBean {

        public static final AdviceBean ME = BeanFactoryUtils.get(AdviceBean.class);

        public void peek(String name) {
            try {
                Object configMap = KernelObject.declaredGet(BeanFactoryUtils.getBeanConfig(), "configMap");
                System.out.println(HelperJson.encode(configMap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Bean
    public static class TestAdvice extends MethodAfter<Boolean> {

        @Override
        public Boolean matching(Class<?> beanType, Method method) {
            return AdviceBean.class.isAssignableFrom(beanType) || UploadCrudFactory.class.isAssignableFrom(beanType);
        }

        @Override
        public void advice(Object proxy, Object returnValue, Method method, Object[] args, Boolean advice) {
            System.out.println("after:" + proxy + returnValue + method);
        }
    }

}
