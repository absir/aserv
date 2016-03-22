/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-3-11 下午5:27:56
 */
package com.absir.aserv.system.server;

import com.absir.aserv.system.helper.HelperServer;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelArray;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverMethod;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.value.Body;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ServerResolverBodys extends ReturnedResolverBody implements ParameterResolver<Object>, ParameterResolverMethod, IServerResolverBody {

    public static final ServerResolverBodys ME = BeanFactoryUtils.get(ServerResolverBodys.class);

    @Value("server.bodys")
    protected boolean bodys;

    public boolean isBodys() {
        return bodys;
    }

    @Override
    public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return ServerResolverBody.ME.getParameter(this, i, parameterNames, parameterTypes, annotations, method);
    }

    @Override
    public Integer getBodyParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        Body bodys = KernelArray.getAssignable(annotations[i], Body.class);
        return bodys == null ? null : bodys.value();
    }

    protected boolean isTraceBody(OnPut onPut) {
        return !bodys || onPut.getInput().isDebug();
    }

    @Override
    public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        return ServerResolverBody.ME.getParameterValue(isTraceBody(onPut) ? ServerResolverBody.ME : this, onPut, parameter, parameterType, beanName, routeMethod);
    }

    @Override
    public Object getBodyParameterValue(OnPut onPut, int group, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        Input input = onPut.getInput();
        InputStream inputStream = input.getInputStream();
        if (inputStream == null) {
            if (input.getInput() == null) {
                return null;

            } else {
                inputStream = new ByteArrayInputStream(input.getInput().getBytes(ContextUtils.getCharset()));
            }
        }

        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        try {
            return ServerResolverBody.ME.getBodyConverter(input).readBodyParameterValue(onPut, group, gzipInputStream, parameterType);

        } finally {
            gzipInputStream.close();
        }
    }

    @Override
    public List<InMethod> resolveMethods(Object parameter, List<InMethod> inMethods) {
        if (inMethods == null) {
            inMethods = new ArrayList<InMethod>();
        }

        if (inMethods.isEmpty()) {
            inMethods.add(InMethod.POST);
        }

        return inMethods;
    }

    @Override
    public Integer getReturned(Method method) {
        Body body = method.getAnnotation(Body.class);
        return body == null ? null : body.value();
    }

    @Override
    public Integer getReturned(Class<?> beanClass) {
        Body body = beanClass.getAnnotation(Body.class);
        return body == null ? null : body.value();
    }

    @Override
    public void resolveReturnedValue(Object returnValue, Integer returned, OnPut onPut) throws Exception {
        if (isTraceBody(onPut)) {
            ServerResolverBody.ME.resolveReturnedValue(returnValue, returned, onPut);

        } else {
            sResolveReturnedValue(returnValue, returned, onPut);
        }
    }

    public void sResolveReturnedValue(Object returnValue, Integer returned, OnPut onPut) throws Exception {
        if (returnValue != null) {
            ServerResolverBody serverResolverBody = ServerResolverBody.ME;
            Input input = onPut.getInput();
            input.setCharacterEncoding(serverResolverBody.getCharset());
            input.setContentTypeCharset(serverResolverBody.getContentTypeCharset());
            byte[] bufferBytes = ServerResolverBody.ME.getBodyConverter(input).writeAsBytes(onPut, returnValue);
            OutputStream outputStream = input.getOutputStream();
            if (outputStream == null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HelperServer.zipCompress(bufferBytes, 0, bufferBytes.length, byteArrayOutputStream);
                bufferBytes = byteArrayOutputStream.toByteArray();
                input.write(bufferBytes);

            } else {
                HelperServer.zipCompress(bufferBytes, 0, bufferBytes.length, outputStream);
            }
        }
    }
}
