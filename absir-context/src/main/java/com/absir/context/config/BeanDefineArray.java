/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-13 下午5:44:29
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstract;

import java.util.ArrayList;
import java.util.List;

public class BeanDefineArray extends BeanDefineAbstract {

    private List<BeanDefine> beanDefines = new ArrayList<BeanDefine>();

    public List<BeanDefine> getBeanDefines() {
        return beanDefines;
    }

    @Override
    public Class<?> getBeanType() {
        return null;
    }

    @Override
    public BeanScope getBeanScope() {
        return null;
    }

    @Override
    public Object getBeanComponent() {
        return null;
    }

    @Override
    public List<Object> getBeanObject(BeanFactory beanFactory) {
        List<Object> beanList = new ArrayList<Object>();
        for (BeanDefine beanDefine : beanDefines) {
            beanList.add(beanDefine.getBeanObject(beanFactory));
        }

        return beanList;
    }

    public List<Object> getBeanObject(BeanFactory beanFactory, String[] paramNames, Class<?>[] parameterTypes) {
        List<Object> beanList = new ArrayList<Object>();
        int size = beanDefines.size();
        for (int i = 0; i < size; i++) {
            BeanDefine beanDefine = beanDefines.get(i);
            if (beanDefine instanceof BeanDefineReference) {
                beanList.add(((BeanDefineReference) beanDefine).getBeanReference(beanFactory, paramNames[i], parameterTypes[i]));

            } else {
                beanList.add(beanDefine.getBeanObject(beanFactory));
            }
        }

        return beanList;
    }
}
