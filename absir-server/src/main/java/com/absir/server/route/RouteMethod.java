/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-23 下午9:24:27
 */
package com.absir.server.route;

import com.absir.core.kernel.KernelLang;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.on.OnPut;
import com.absir.server.route.invoker.InvokerResolver;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverMethod;
import com.absir.server.route.returned.ReturnedResolver;

import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RouteMethod {

    Class<?>[] parameterTypes;

    Object[] parameters;

    ParameterResolver[] parameterResolvers;

    Object[] invokers;

    InvokerResolver[] invokerResolvers;

    String[] beanNames;

    boolean[] nullAbles;

    /**
     * 返回值不作为服务返回值
     */
    boolean noBody;

    Object returned;

    ReturnedResolver returnedResolver;

    private Method method;

    protected RouteMethod(Method beanMethod) {
        this.method = beanMethod;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public ParameterResolver[] getParameterResolvers() {
        return parameterResolvers;
    }

    public List<InMethod> resolveMethods(List<InMethod> inMethods) {
        if (parameterResolvers != null) {
            int length = parameterResolvers.length;
            for (int i = 0; i < length; i++) {
                ParameterResolver parameterResolver = parameterResolvers[i];
                if (parameterResolver != null && parameterResolver instanceof ParameterResolverMethod) {
                    inMethods = ((ParameterResolverMethod) parameterResolver).resolveMethods(parameters[i], inMethods);
                }
            }
        }

        return inMethods;
    }

    public void invoke(Object routeBean, OnPut onPut, boolean returnedFixed) throws Throwable {
        int length = beanNames == null ? 0 : beanNames.length;
        Object parameterValue;
        Object[] parameterValues = length == 0 ? KernelLang.NULL_OBJECTS : new Object[length];
        for (int i = 0; i < length; i++) {
            parameterValue = parameterResolvers[i].getParameterValue(onPut, parameters[i], parameterTypes[i], beanNames[i], this);
            if (parameterValue == null && !nullAbles[i]) {
                throw new ServerException(ServerStatus.IN_404);
            }

            parameterValues[i] = parameterValue;
        }

        length = invokers == null ? 0 : invokers.length;
        for (int i = 0; i < length; i++) {
            invokerResolvers[i].resolveBefore(invokers[0], onPut);
        }

        onPut.setReturnedFixed(returnedFixed);
        parameterValue = method.invoke(routeBean, parameterValues);
        if (!noBody && method.getReturnType() != void.class) {
            onPut.setReturnValue(parameterValue);
        }

        if (returnedResolver != null && !onPut.isReturnedFixed()) {
            onPut.setReturned(returned);
            onPut.setReturnedResolver(returnedResolver);
        }

        for (int i = 0; i < length; i++) {
            invokerResolvers[i].resolveAfter(parameterValue, invokers[0], onPut);
        }
    }
}
