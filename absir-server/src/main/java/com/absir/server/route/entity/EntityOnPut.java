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

@Configure
public class EntityOnPut extends RouteEntity {

    @Value(value = "onPut.lifeTime")
    private static long lifeTime = 600000;

    private BeanDefine beanDefine;

    private Map<Serializable, ContextOnPut> idMapBeanObject = new HashMap<Serializable, ContextOnPut>();

    public EntityOnPut(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    @Override
    public Class<?> getRouteType() {
        return beanDefine.getBeanType();
    }

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

    public class ContextOnPut extends ContextBase {

        private Serializable id;

        private Object beanObject;

        public ContextOnPut(Serializable id, Object beanObject) {
            this.id = id;
            this.beanObject = beanObject;
        }

        protected long getLifeTime() {
            return lifeTime;
        }

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
