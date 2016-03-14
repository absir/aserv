/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import com.absir.aop.value.Proxy;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelCollection;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author absir
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AopProxyUtils {

    /**
     * enhancerClass
     */
    private static Class<?> enhancerClass = KernelClass.forName("net.sf.cglib.proxy.Enhancer");

    /**
     * aopProxyClass
     */
    private static Class<?>[] aopProxyClass = new Class<?>[]{AopProxy.class};

    /**
     * @param beanObject
     * @return
     */
    public static <T> T getProxy(T beanObject) {
        return (T) getProxy(beanObject, false, false);
    }

    /**
     * @param beanObject
     * @param jdk
     * @param impl
     * @return
     */
    public static AopProxy getProxy(Object beanObject, boolean jdk, boolean impl) {
        return getProxy(beanObject, null, jdk, impl);
    }

    /**
     * @param beanObject
     * @param interfaces
     * @param jdk
     * @param impl
     * @return
     */
    public static AopProxy getProxy(Object beanObject, Set<Class<?>> interfaces, boolean jdk, boolean impl) {
        return getProxy(beanObject, null, interfaces, jdk, impl);
    }

    /**
     * @param beanObject
     * @param beanType
     * @param interfaces
     * @param jdk
     * @param impl
     * @return
     */
    public static AopProxy getProxy(Object beanObject, Class<?> beanType, Set<Class<?>> interfaces, boolean jdk,
                                    boolean impl) {
        if (interfaces == null) {
            interfaces = new HashSet<Class<?>>();
        }

        if (beanObject != null) {
            if (beanObject instanceof AopProxy) {
                Class<?> aopClass = beanObject.getClass();
                if (beanType != null) {
                    if (!beanType.isAssignableFrom(aopClass)) {
                        aopClass = null;
                    }
                }

                if (aopClass != null && interfaces != null) {
                    for (Class<?> inter : interfaces) {
                        if (!inter.isAssignableFrom(aopClass)) {
                            aopClass = null;
                            break;
                        }
                    }
                }

                if (aopClass != null) {
                    return (AopProxy) beanObject;
                }
            }

            Class<?> beanClass = beanObject.getClass();
            if (beanType == null) {
                beanType = beanClass;
            }

            while (beanClass != null && beanClass != Object.class) {
                for (Class<?> iCls : beanClass.getInterfaces()) {
                    interfaces.add(iCls);
                }

                beanClass = beanClass.getSuperclass();
            }
        }

        if (beanType == null) {
            impl = false;

        } else {
            if (beanType.isInterface()) {
                impl = false;
                interfaces.add(beanType);

            } else if (enhancerClass == null || Modifier.isFinal(beanType.getModifiers())) {
                impl = false;
            }
        }

        Class<?>[] iterfaceArray = null;
        if (interfaces.isEmpty()) {
            iterfaceArray = aopProxyClass;

        } else {
            interfaces.add(AopProxy.class);
            iterfaceArray = KernelCollection.toArray(interfaces, Class.class);
        }

        if (jdk) {
            return (AopProxy) java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    iterfaceArray, new AopProxyJDK(beanType, beanObject));
        }

        Enhancer enhancer = new Enhancer();
        if (impl && beanType != null) {
            enhancer.setSuperclass(beanType);
        }

        enhancer.setInterfaces(iterfaceArray);
        enhancer.setCallback(new AopProxyCglib(beanType, beanObject));
        return (AopProxy) enhancer.create();
    }

    /**
     * @param beanObject
     * @return
     */
    public static <T> T getBean(T beanObject) {
        T bean;
        if (beanObject instanceof AopProxy) {
            bean = (T) ((AopProxy) beanObject).getBeanObject();
            if (bean != null) {
                return bean;
            }
        }

        return beanObject;
    }

    /**
     * @param beanObject
     * @return
     */
    public static Class<?> getBeanType(Object beanObject) {
        if (beanObject instanceof AopProxy) {
            AopProxy aopProxy = (AopProxy) beanObject;
            Class<?> beanType = aopProxy.getBeanType();
            if (beanType != null) {
                return beanType;
            }

            beanObject = aopProxy.getBeanObject();
            if (beanObject != null) {
                return beanObject.getClass();
            }

            return aopProxy.getBeanType();
        }

        return beanObject.getClass();
    }

    /**
     * @param beanObject
     * @param aopInterceptors
     * @return
     */
    public static AopProxy proxyInterceptors(Object beanObject, Collection<AopInterceptor> aopInterceptors) {
        return proxyInterceptors(beanObject, null, aopInterceptors);
    }

    /**
     * @param beanObject
     * @param beanType
     * @param aopInterceptor
     * @return
     */
    public static AopProxy proxyInterceptors(Object beanObject, Class<?> beanType,
                                             Collection<AopInterceptor> aopInterceptors) {
        AopProxy aopProxy = null;
        Set<Class<?>> interfaces = null;
        if (aopInterceptors != null) {
            interfaces = new HashSet<Class<?>>();
            for (AopInterceptor aopInterceptor : aopInterceptors) {
                Class<?> iCls = aopInterceptor.getInterface();
                if (iCls != null) {
                    interfaces.add(iCls);
                }
            }

            if (interfaces.isEmpty()) {
                interfaces = null;
            }
        }

        if (beanObject == null || !(beanObject instanceof AopProxy)) {
            if (beanType == null) {
                beanType = beanObject.getClass();
            }

            Proxy proxy = beanType.getAnnotation(Proxy.class);
            aopProxy = getProxy(beanObject, beanType, interfaces, proxy == null ? false : proxy.jdk(),
                    proxy == null ? true : proxy.impl());

        } else {
            aopProxy = (AopProxy) beanObject;
            if (interfaces != null) {
                Proxy proxy = beanType.getAnnotation(Proxy.class);
                aopProxy = getProxy(aopProxy.getBeanObject(), beanType, interfaces, proxy == null ? false : proxy.jdk(),
                        proxy == null ? true : proxy.impl());
            }
        }

        if (aopInterceptors != null) {
            aopProxy.getAopInterceptors().addAll(aopInterceptors);
        }

        return aopProxy;
    }
}
