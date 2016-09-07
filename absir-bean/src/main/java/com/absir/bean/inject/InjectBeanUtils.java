package com.absir.bean.inject;

import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Orders;

/**
 * Created by absir on 16/9/5.
 */
@Configure
public class InjectBeanUtils {

    @Orders
    @Inject
    protected static IBeanProxy[] beanProxies;

    public static Object getBeanObject(Object proxy) {
        if (beanProxies == null || beanProxies.length == 0) {
            return proxy;
        }

        Object bean = proxy;
        for (IBeanProxy beanProxy : beanProxies) {
            bean = beanProxy.getBeanObject(proxy);
            if (bean == null) {
                return proxy;
            }

            proxy = bean;
        }

        return bean;
    }

    public static Class<?> getBeanType(Object proxy) {
        return getBeanObject(proxy).getClass();
    }
}
