/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-20 上午10:50:39
 */
package com.absir.server.route;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.core.*;
import com.absir.bean.inject.IMethodEntry;
import com.absir.bean.inject.InjectBeanDefine;
import com.absir.bean.inject.InjectBeanFactory;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyUtils;
import com.absir.server.in.InMethod;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnScope;
import com.absir.server.route.entity.EntityMutil;
import com.absir.server.route.entity.EntityOnPut;
import com.absir.server.route.entity.EntitySingleton;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverPath;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.value.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Basis
@Bean
public class RouteFactory implements IBeanDefineSupply, IBeanFactoryAware, IMethodEntry<Object> {

    public static final RouteFactory ME = BeanFactoryUtils.get(RouteFactory.class);

    @Inject
    RouteMapping routeMapping;

    @Inject(type = InjectType.Selectable)
    @Orders
    ParameterResolver[] parameterResolvers;

    ParameterResolverPath parameterResolverPath = new ParameterResolverPath();

    @Inject(type = InjectType.Selectable)
    @Orders
    ReturnedResolver[] returnedResolvers;

    private Map<Method, RouteMethod> methodMapRouteMethod = new HashMap<Method, RouteMethod>();

    private Map<Class<?>, RouteEntry> beanTypeMapRouteEntry = new HashMap<Class<?>, RouteEntry>();

    public static boolean isMethodServering(Method method) {
        return Modifier.isPublic(method.getModifiers()) && !(Modifier.isStatic(method.getModifiers())
                || method.getName().charAt(0) == '_' || BeanConfigImpl.findMethodAnnotation(method, Close.class, true));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        if (!BeanDefineOriginal.isAbstractBeanType(beanType)) {
            Server server = BeanConfigImpl.getTypeAnnotation(beanType, Server.class);
            if (server != null) {
                RouteDefine routeDefine = new RouteDefine(
                        new InjectBeanDefine(new BeanDefineType(beanType),
                                server.scope() == OnScope.SINGLETON ? BeanScope.SINGLETON : BeanScope.PROTOTYPE),
                        server.scope());
                return InjectBeanFactory.getInstance().getBeanDefines(beanFactory, beanType, routeDefine);
            }
        }

        return null;
    }

    @Override
    public Object getMethod(Class<?> beanType, Method method) {
        Before before = method.getAnnotation(Before.class);
        if (before != null) {
            return before;
        }

        After after = method.getAnnotation(After.class);
        if (after != null) {
            return after;
        }

        OnException onException = method.getAnnotation(OnException.class);
        if (onException != null && onException.value().length > 0) {
            return onException;
        }

        return null;
    }

    @Override
    public void setMethodEntry(Object define, Class<?> beanType, Method beanMethod, Method method) {
        RouteEntry routeEntry = beanTypeMapRouteEntry.get(beanType);
        if (routeEntry == null) {
            routeEntry = new RouteEntry();
            beanTypeMapRouteEntry.put(beanType, routeEntry);
        }

        Class<?> beanClass = beanMethod.getDeclaringClass();
        if (beanType != beanClass) {
            RouteEntry routeMethodEntry = beanTypeMapRouteEntry.get(beanClass);
            if (routeMethodEntry == null) {
                routeMethodEntry = new RouteEntry();
                beanTypeMapRouteEntry.put(beanClass, routeMethodEntry);
            }

            routeEntry.addRouteEntries(routeMethodEntry);
            routeEntry = routeMethodEntry;
        }

        if (methodMapRouteMethod.get(beanMethod) == null) {
            RouteMethod routeMethod = getRouteMethod(beanType, beanMethod, method);
            methodMapRouteMethod.put(beanMethod, routeMethod);
            if (define instanceof Before) {
                routeEntry.addBeforeMethod(routeMethod);

            } else if (define instanceof After) {
                routeEntry.addAfterMethod(routeMethod);

            } else if (define instanceof OnException) {
                routeEntry.addRouteException(new RouteException(((OnException) define).value(), routeMethod));
            }
        }
    }

    protected RouteEntry getRouteEntry(Class<?> beanType) {
        RouteEntry routeEntry = beanTypeMapRouteEntry.get(beanType);
        if (routeEntry == null) {
            routeEntry = new RouteEntry();
            beanTypeMapRouteEntry.put(beanType, routeEntry);
            Class<?> beanClass = beanType;
            Interceptors interceptors;
            while (beanClass != null && beanClass != Object.class) {
                interceptors = BeanConfigImpl.getTypeAnnotation(beanClass, Interceptors.class);
                if (interceptors != null) {
                    for (Class<? extends Interceptor> interceptorClass : interceptors.value()) {
                        routeEntry.addInterceptor(BeanFactoryUtils.getRegisterBeanObject(interceptorClass));
                    }
                }

                beanClass = beanClass.getSuperclass();
            }

            InjectBeanFactory.getInstance().getMethodEntries(beanType, this);
            routeEntry = routeEntry.getRouteEntry();
        }

        return routeEntry;
    }

    protected RouteMethod getRouteMethod(Class<?> beanType, Method beanMethod, Method method) {
        return getRouteMethod(beanType, beanMethod, method, null, null, null);
    }

    protected RouteMethod getRouteMethod(Class<?> beanType, Method beanMethod, Method method,
                                         List<String> parameterPathNames, List<Integer> parameterPathIndexs,
                                         List<Annotation[]> parameterPathAnnotations) {
        RouteMethod routeMethod = new RouteMethod(beanMethod);
        Annotation[][] parameterAnnotations = beanMethod.getParameterAnnotations();
        int length = parameterAnnotations.length;
        String[] parameterNames = BeanDefineDiscover.parameterNames(beanMethod, parameterAnnotations);
        routeMethod.parameterTypes = beanMethod.getParameterTypes();
        routeMethod.parameters = new Object[length];
        routeMethod.parameterResolvers = new ParameterResolver[length];
        routeMethod.beanNames = PropertyUtils.parameterBeanNames(parameterAnnotations);
        routeMethod.nullAbles = new boolean[length];
        routeMethod.noBody = (method == null ? beanMethod : method).getAnnotation(NoBody.class) != null;
        for (int i = 0; i < length; i++) {
            Object parameter = null;
            if (parameterResolvers != null) {
                for (ParameterResolver parameterResolver : parameterResolvers) {
                    parameter = parameterResolver.getParameter(i, parameterNames, routeMethod.parameterTypes,
                            parameterAnnotations, beanMethod);
                    if (parameter != null) {
                        routeMethod.parameters[i] = parameter;
                        routeMethod.parameterResolvers[i] = parameterResolver;
                        break;
                    }
                }
            }

            if (parameterPathAnnotations != null
                    && KernelArray.getAssignable(parameterAnnotations[i], Nullable.class) != null) {
                routeMethod.nullAbles[i] = true;
            }

            if (parameter == null) {
                parameter = parameterResolverPath.getParameter(i, parameterNames, routeMethod.parameterTypes,
                        parameterAnnotations, beanMethod);
                if (parameterPathNames != null) {
                    parameterPathNames.add((String) parameter);
                }

                if (parameterPathIndexs != null) {
                    parameterPathIndexs.add(i);
                }

                if (parameterPathAnnotations != null) {
                    parameterPathAnnotations.add(parameterAnnotations[i]);
                }

                routeMethod.parameters[i] = parameter;
                routeMethod.parameterResolvers[i] = parameterResolverPath;
            }
        }

        if (returnedResolvers != null) {
            while (beanMethod != null) {
                for (ReturnedResolver returnedResolver : returnedResolvers) {
                    routeMethod.returned = returnedResolver.getReturned(beanMethod);
                    if (routeMethod.returned != null) {
                        routeMethod.returnedResolver = returnedResolver;
                        return routeMethod;
                    }
                }

                beanMethod = beanMethod == method ? null : method;
            }

            while (beanType != null && beanType != Object.class) {
                for (ReturnedResolver returnedResolver : returnedResolvers) {
                    routeMethod.returned = returnedResolver.getReturned(beanType);
                    if (routeMethod.returned != null) {
                        routeMethod.returnedResolver = returnedResolver;
                        return routeMethod;
                    }
                }

                beanType = beanType.getSuperclass();
            }
        }

        return routeMethod;
    }

    @Override
    public void beforeRegister(BeanFactoryImpl beanFactory) {
    }

    @Override
    public void afterRegister(BeanFactoryImpl beanFactory) {
        List<BeanDefine> beanDefines = beanFactory.getBeanDefineList(RouteDefine.class);
        int size = beanDefines.size();
        for (int i = 0; i < size; i++) {
            BeanDefine beanDefine = beanDefines.get(i);
            if (BeanConfigImpl.findTypeAnnotation(beanDefine.getBeanType(), Close.class)) {
                continue;
            }

            RouteDefine routeDefine = BeanFactoryImpl.getBeanDefine(beanDefine, RouteDefine.class);
            Object beanObject = beanDefine.getBeanObject(beanFactory);
            beanDefine = beanFactory.getBeanDefine(beanDefine.getBeanName());
            beanDefines.set(i, beanDefine);
            Set<Method> beanMethods = new HashSet<Method>();
            if (beanObject != null) {
                String name = null;
                RouteEntry routeEntry = null;
                RouteEntity routeEntity = null;
                IRoute iRoute = null;
                Mapping mapping = null;
                boolean urlDecode = false;
                Class<?> beanType = beanDefine.getBeanType();
                Class<?> beanClass = beanType;
                while (beanClass != null && beanClass != Object.class) {
                    for (Method method : beanClass.getDeclaredMethods()) {
                        if (isMethodServering(method)) {
                            if (name == null) {
                                name = KernelString.lastString(beanDefine.getBeanType().getSimpleName(), '_');
                                routeEntry = getRouteEntry(beanType);
                                routeEntity = getRouteEntity(beanObject, beanDefine, routeDefine);
                                iRoute = routeEntry == null ? null : routeEntry.getIRoute();
                                if (iRoute == null) {
                                    iRoute = routeMapping;
                                }

                                Class<?> routeClass = beanType;
                                while (routeClass != null && routeClass != Object.class) {
                                    mapping = BeanConfigImpl.getTypeAnnotation(routeClass, Mapping.class);
                                    if (mapping != null) {
                                        break;
                                    }

                                    routeClass = routeClass.getSuperclass();
                                }

                                routeClass = beanType;
                                while (routeClass != null && routeClass != Object.class) {
                                    if (BeanConfigImpl.findTypeAnnotation(routeClass, UrlDecode.class)) {
                                        urlDecode = true;
                                        break;

                                    } else if (BeanConfigImpl.findTypeAnnotation(routeClass, UrlBase.class)) {
                                        break;
                                    }

                                    routeClass = routeClass.getSuperclass();
                                }
                            }

                            if (!beanMethods.add(InjectBeanFactory.getInstance().getBeanMethod(beanType, method))) {
                                continue;
                            }

                            List<String> parameterPathNames = new ArrayList<String>();
                            List<Integer> parameterPathIndexs = new ArrayList<Integer>();
                            List<Annotation[]> parameterPathAnnotations = new ArrayList<Annotation[]>();
                            RouteMethod routeMethod = getRouteMethod(beanType, method, null, parameterPathNames,
                                    parameterPathIndexs, parameterPathAnnotations);
                            List<String> mappings = new ArrayList<String>();
                            List<InMethod> inMethods = new ArrayList<InMethod>();
                            iRoute.routeMapping(name, new ObjectEntry<Mapping, List<String>>(mapping, null), method,
                                    parameterPathNames, mappings, inMethods);
                            if (mappings.size() > 0) {
                                boolean url = (urlDecode || BeanConfigImpl.findMethodAnnotation(method, UrlDecode.class))
                                        && !BeanConfigImpl.findMethodAnnotation(method, UrlBase.class);
                                String[] parameterPathNameArray = KernelCollection.toArray(parameterPathNames,
                                        String.class);
                                routeMapping.routeMapping(
                                        new RouteAction(url, routeEntity, routeEntry, routeMethod,
                                                parameterPathNameArray, parameterPathIndexs, parameterPathAnnotations),
                                        parameterPathNameArray, mappings, inMethods);
                            }
                        }
                    }

                    beanClass = beanClass.getSuperclass();
                }
            }
        }

        // 完成路由，清除中间件
        for (BeanDefine beanDefine : beanDefines) {
            if (beanDefine != null) {
                beanFactory.unRegisterBeanDefine(beanDefine);
            }
        }

        routeMapping.getRouteAdapter().registerAllMatcher();
    }

    protected RouteEntity getRouteEntity(final Object beanObject, final BeanDefine beanDefine,
                                         RouteDefine routeDefine) {
        if (routeDefine.getOnScope() == OnScope.MULTI) {
            return new EntityMutil(beanDefine);
        }

        if (routeDefine.getOnScope() == OnScope.ONPUT) {
            return new EntityOnPut(beanDefine);
        }

        return new EntitySingleton(beanObject);
    }
}
