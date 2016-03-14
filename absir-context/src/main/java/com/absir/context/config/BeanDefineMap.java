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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 */
public class BeanDefineMap extends BeanDefineAbstract {

    /**
     * beanDefines
     */
    private Map<String, BeanDefine> beanDefines = new HashMap<String, BeanDefine>();

    /**
     * @return the beanDefines
     */
    public Map<String, BeanDefine> getBeanDefines() {
        return beanDefines;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanType()
     */
    @Override
    public Class<?> getBeanType() {
        return Map.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanName()
     */
    @Override
    public String getBeanName() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanScope()
     */
    @Override
    public BeanScope getBeanScope() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanComponent()
     */
    @Override
    public Object getBeanComponent() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory)
     */
    @Override
    public Map<String, Object> getBeanObject(BeanFactory beanFactory) {
        Map<String, Object> beanMap = new HashMap<String, Object>();
        for (Entry<String, BeanDefine> entry : beanDefines.entrySet()) {
            beanMap.put(entry.getKey(), entry.getValue().getBeanObject(beanFactory));
        }

        return beanMap;
    }

}
