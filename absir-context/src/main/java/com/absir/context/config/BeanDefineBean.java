/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-13 下午4:17:14
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstractor;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectInvoker;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BeanDefineBean extends BeanDefineAbstractor {

    protected Class<?> beanType;

    private Constructor<?> constructor;

    private String[] paramNames;

    private BeanDefineArray constructorBeanDefine;

    private List<InjectInvoker> injectInvokers = new ArrayList<InjectInvoker>();

    public BeanDefineBean(Class<?> beanType, String beanName, BeanDefineArray constructorBeanDefine) {
        this.beanType = beanType;
        this.beanName = BeanDefineType.getBeanName(beanName, beanType);
        this.constructor = BeanDefineType.getBeanConstructor(beanType, constructorBeanDefine == null ? 0 : constructorBeanDefine
                .getBeanDefines().size());
        this.paramNames = BeanDefineDiscover.parameterNames(constructor);
        this.constructorBeanDefine = paramNames == null ? null : constructorBeanDefine;
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.SINGLETON;
    }

    @Override
    public Object getBeanComponent() {
        return constructor;
    }

    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(constructor.getDeclaringClass().getName());
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        Object beanObject = null;
        try {
            if (paramNames == null) {
                beanObject = constructor.newInstance();

            } else {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                List<Object> beanObjects = constructorBeanDefine.getBeanObject(beanFactory);
                int size = beanObjects.size();
                for (int i = 0; i < size; i++) {
                    beanObjects.set(i, DynaBinder.to(beanObjects.get(i), paramNames[i], parameterTypes[i]));
                }

                beanObject = constructor.newInstance(beanObjects.toArray());
            }

        } catch (Exception e) {
            if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0) {
                e.printStackTrace();
            }
        }

        return beanObject;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        Object beanObject = super.getBeanObject(beanFactory, beanDefineRoot, beanDefineWrapper);
        if (beanObject != null) {
            for (InjectInvoker injectInvoker : injectInvokers) {
                injectInvoker.invoke(beanFactory, beanObject);
            }
        }

        return beanObject;
    }

    public List<InjectInvoker> getInjectInvokers() {
        return injectInvokers;
    }
}
