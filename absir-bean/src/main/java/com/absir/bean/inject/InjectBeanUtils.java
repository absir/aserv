package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelList;

import java.util.List;

/**
 * Created by absir on 16/9/5.
 */
public class InjectBeanUtils {

    protected static IBeanProxy[] beanProxies;

    public static Object getBeanObject(Object proxy) {
        if (beanProxies == null) {
            BeanFactory beanFactory = BeanFactoryUtils.get();
            if (beanFactory != null) {
                List<IBeanProxy> beanProxyList = beanFactory.getBeanObjects(IBeanProxy.class);
                if (beanProxyList != null && !beanProxyList.isEmpty()) {
                    KernelList.sortCommonObjects(beanProxyList);
                    beanProxies = KernelCollection.toArray(beanProxyList, IBeanProxy.class);
                }
            }
        }

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
