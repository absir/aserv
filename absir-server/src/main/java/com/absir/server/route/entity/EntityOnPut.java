/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 下午12:51:25
 */
package com.absir.server.route.entity;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextBase;
import com.absir.context.core.ContextUtils;
import com.absir.server.in.Input;
import com.absir.server.route.RouteEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
@Configure
public class EntityOnPut extends RouteEntity {

    @Value(value = "onPut.lifeTime")
    private static long lifeTime = 600000;

    /**
     * beanDefine
     */
    private BeanDefine beanDefine;

    /**
     * idMapBeanObject
     */
    private Map<Serializable, ContextOnPut> idMapBeanObject = new HashMap<Serializable, ContextOnPut>();

    /**
     * @param beanDefine
     */
    public EntityOnPut(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.RouteEntity#getRouteType()
     */
    @Override
    public Class<?> getRouteType() {
        return beanDefine.getBeanType();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.RouteEntity#getRouteBean(com.absir.server.in.Input
     * )
     */
    @Override
    public Object getRouteBean(Input input) {
        Serializable id = input.getId();
        ContextOnPut contextOnPut = idMapBeanObject.get(id);
        if (contextOnPut == null) {
            synchronized (idMapBeanObject) {
                contextOnPut = idMapBeanObject.get(id);
                if (contextOnPut == null) {
                    contextOnPut = new ContextOnPut(id, beanDefine.getBeanObject(BeanFactoryUtils.get()));
                    ContextUtils.getContextFactory().addContext(contextOnPut);
                    return contextOnPut.beanObject;
                }
            }
        }

        contextOnPut.retainAt();
        return contextOnPut.beanObject;
    }

    /**
     * @author absir
     */
    public class ContextOnPut extends ContextBase {

        /**
         * id
         */
        private Serializable id;

        /**
         * beanObject
         */
        private Object beanObject;

        /**
         * @param id
         * @param beanObject
         */
        public ContextOnPut(Serializable id, Object beanObject) {
            this.id = id;
            this.beanObject = beanObject;
        }

        /**
         * @return
         */
        protected long getLifeTime() {
            return lifeTime;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.context.core.ContextBase#uninitialize()
         */
        @Override
        public void uninitialize() {
            if (isExpiration()) {
                idMapBeanObject.remove(id);

            } else {
                ContextUtils.getContextFactory().addContext(this);
            }
        }
    }
}
