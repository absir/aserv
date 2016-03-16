/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午5:25:49
 */
package com.absir.aop;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstractor;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;

import java.util.List;

public class AopImplDefine extends BeanDefineAbstractor {

    private Class<?> beanType;

    private BeanScope beanScope;

    private String implName;

    public AopImplDefine(String beanName, Class<?> beanType, BeanScope beanScope, String implName) {
        this.beanName = beanName;
        this.beanType = beanType;
        this.beanScope = beanScope == null ? BeanScope.SINGLETON : beanScope;
        this.implName = KernelString.isEmpty(implName) ? null : implName;
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    public String getName() {
        return implName;
    }

    @Override
    public BeanScope getBeanScope() {
        return beanScope;
    }

    @Override
    public Object getBeanComponent() {
        return beanType;
    }

    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(beanType.getName());
    }

    private boolean circleBeanDefine(BeanDefine beanDefine, Class<?> cls) {
        return beanType == beanDefine.getBeanType()
                || !(BeanFactoryImpl.getBeanDefine(beanDefine, AopImplDefine.class) == null || beanDefine.getBeanType() == cls);
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        BeanDefine beanDefine = null;
        if (beanType.isInterface()) {
            for (Class<?> cls : beanType.getInterfaces()) {
                if (implName == null) {
                    List<BeanDefine> beanDefines = beanFactory.getBeanDefines(cls);
                    if (beanDefines.size() > 1) {
                        for (BeanDefine define : beanDefines) {
                            if (circleBeanDefine(define, cls)) {
                                continue;
                            }

                            beanDefine = define;
                            break;
                        }
                    }

                } else {
                    beanDefine = beanFactory.getBeanDefine(implName, cls);
                    if (beanDefine != null && circleBeanDefine(beanDefine, cls)) {
                        beanDefine = null;
                    }
                }

                if (beanDefine != null) {
                    break;
                }
            }
        }

        return AopProxyUtils.proxyInterceptors(beanDefine == null ? null : beanDefine.getBeanObject(beanFactory), beanType, null);
    }
}
