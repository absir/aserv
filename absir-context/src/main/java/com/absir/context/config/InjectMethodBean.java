/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-13 下午7:17:35
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectInvoker;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;

import java.lang.reflect.Method;
import java.util.List;

public class InjectMethodBean extends InjectInvoker {

    private Method method;

    private String[] paramNames;

    private BeanDefineArray beanDefineArray;

    public InjectMethodBean(Method method, BeanDefineArray beanDefineArray) {
        this.method = method;
        this.paramNames = BeanDefineDiscover.paramterNames(method);
        this.beanDefineArray = paramNames == null ? null : beanDefineArray;
    }

    @Override
    public void invoke(BeanFactory beanFactory, Object beanObject) {
        try {
            if (paramNames == null) {
                method.invoke(beanObject);

            } else {
                List<Object> beanObjects = beanDefineArray.getBeanObject(beanFactory);
                int size = beanObjects.size();
                Class<?>[] parameterTypes = method.getParameterTypes();
                boolean invoke = false;
                for (int i = 0; i < size; i++) {
                    Object value = beanObjects.get(i);
                    if (value != null) {
                        invoke = true;
                    }

                    beanObjects.set(i, DynaBinder.to(value, paramNames[i], parameterTypes[i]));
                }

                if (invoke) {
                    method.invoke(beanObject, beanObjects.toArray());
                }
            }

        } catch (Exception e) {
            if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0) {
                e.printStackTrace();
            }
        }
    }
}
