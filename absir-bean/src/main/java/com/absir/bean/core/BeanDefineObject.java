/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年1月27日 下午12:10:45
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.config.IBeanObject;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;

import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class BeanDefineObject extends BeanDefineAbstractor {

    private Method beanMethod;

    private Class<?> beanType;

    private BeanDefine beanDefine;

    public BeanDefineObject(Method method, BeanDefine beanDefine) {
        beanMethod = method;
        beanType = method.getReturnType();
        this.beanDefine = beanDefine;
    }

    public static BeanDefine getBeanDefine(Class<?> beanType, BeanDefine beanDefine) {
        if (BeanFactory.class.isAssignableFrom(beanType)) {
            Method method = KernelReflect.method(beanType, "getBeanObject");
            if (method != null) {
                return new BeanDefineObject(method, beanDefine);
            }
        }

        return beanDefine;
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public BeanScope getBeanScope() {
        return beanDefine.getBeanScope();
    }

    @Override
    public Object getBeanComponent() {
        return beanMethod;
    }

    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(beanMethod.getDeclaringClass().getName());
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        IBeanObject factoryBean = (IBeanObject) beanDefine.getBeanObject(beanFactory);
        return factoryBean == null ? null : factoryBean.getBeanObject();
    }
}
