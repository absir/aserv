/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-11 下午5:27:56
 */
package com.absir.aserv.system.server;

import com.absir.aserv.system.server.value.Result;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.binder.BinderData;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.route.body.IBodyConverter;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverMethod;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.value.Body;
import com.absir.servlet.InputRequest;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 *
 */
@Base
@Bean
public class ServerResolverBody extends ReturnedResolverBody implements ParameterResolver<Object>, ParameterResolverMethod, IServerResolverBody, IBodyConverter {

    /**
     * ME
     */
    public static final ServerResolverBody ME = BeanFactoryUtils.get(ServerResolverBody.class);
    /**
     * BODY_OBJECT_NAME
     */
    protected static final String BODY_OBJECT_NAME = ServerResolverBody.class.getName() + "@BODY_OBJECT_NAME";
    /**
     * objectMapper
     */
    protected ObjectMapper objectMapper;

    /**
     * @return the objectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @param bodyConverters
     */
    @Inject(type = InjectType.Selectable)
    protected void initResolver(IBodyConverter[] bodyConverters) {
        super.initResolver(bodyConverters);
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.returned.ReturnedResolverBody#getBodyConverter
     * (com.absir.server.in.Input)
     */
    @Override
    public IBodyConverter getBodyConverter(Input input) {
        Object converter = input.getAttribute(BODY_CONVERTER_NAME);
        if (converter == null) {
            IBodyConverter bodyConverter = null;
            if (input instanceof InputRequest) {
                String contentType = ((InputRequest) input).getRequest().getContentType();
                if (contentType != null) {
                    bodyConverter = typeMapConverter.get(contentType);
                }
            }

            if (bodyConverter == null) {
                bodyConverter = this;
            }

            setBodyConverter(input, bodyConverter);
            return bodyConverter;

        } else if (!(converter instanceof IBodyConverter)) {
            return this;
        }

        return (IBodyConverter) converter;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
     * java.lang.String[], java.lang.Class<?>[],
     * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
     */
    @Override
    public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return getParameter(this, i, parameterNames, parameterTypes, annotations, method, true);
    }

    /**
     * @param body
     * @param i
     * @param parameterNames
     * @param parameterTypes
     * @param annotations
     * @param method
     * @return
     */
    public Object getParameter(IServerResolverBody body, int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return getParameter(body, i, parameterNames, parameterTypes, annotations, method, false);
    }

    /**
     * @param body
     * @param i
     * @param parameterNames
     * @param parameterTypes
     * @param annotations
     * @param method
     * @param defaultBody
     * @return
     */
    protected Object getParameter(IServerResolverBody body, int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method, boolean defaultBody) {
        Integer value = body.getBodyParameter(i, parameterNames, parameterTypes, annotations, method);
        if (defaultBody || value != null) {
            Result binderResult = KernelArray.getAssignable(annotations[i], Result.class);
            return binderResult == null ? value : binderResult;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerResolverBody#getBodyParameter(int,
     * java.lang.String[], java.lang.Class[],
     * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
     */
    @Override
    public Integer getBodyParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        Body body = KernelArray.getAssignable(annotations[i], Body.class);
        return body == null ? null : body.value();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.parameter.ParameterResolver#getParameterValue(
     * com.absir.server.on.OnPut, java.lang.Object, java.lang.Class,
     * java.lang.String, com.absir.server.route.RouteMethod)
     */
    @Override
    public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        return getParameterValue(this, onPut, parameter, parameterType, beanName, routeMethod);
    }

    /**
     * @param body
     * @param onPut
     * @param parameter
     * @param parameterType
     * @param beanName
     * @param routeMethod
     * @return
     * @throws Exception
     */
    public Object getParameterValue(IServerResolverBody body, OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        if (parameter instanceof Result) {
            Object bodyObject = onPut.getInput().getAttribute(BODY_OBJECT_NAME);
            if (bodyObject == null) {
                bodyObject = body.getBodyParameterValue(onPut, 0, Object.class, null, routeMethod);
                onPut.getInput().setAttribute(BODY_OBJECT_NAME, bodyObject);
            }

            Result result = (Result) parameter;
            BinderData binderData = onPut.getBinderData();
            binderData.getBinderResult().setGroup(result.group());
            binderData.getBinderResult().setValidation(result.validation());
            String name = result.name();
            return binderData.bind(KernelString.isEmpty(name) || !(bodyObject instanceof Map) ? bodyObject : ((Map<?, ?>) bodyObject).get(name), beanName, parameterType);

        } else {
            return body.getBodyParameterValue(onPut, (Integer) parameter, parameterType, beanName, routeMethod);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerResolverBody#getBodyParameterValue
     * (com.absir.server.on.OnPut, int, java.lang.Class, java.lang.String,
     * com.absir.server.route.RouteMethod)
     */
    @Override
    public Object getBodyParameterValue(OnPut onPut, int group, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        Input input = onPut.getInput();
        if (parameterType == String.class) {
            return input.getInput();
        }

        InputStream inputStream = input.getInputStream();
        IBodyConverter converter = getBodyConverter(input);
        return inputStream == null ? converter.readBodyParameterValue(onPut, group, input.getInput(), parameterType) : converter.readBodyParameterValue(onPut, group, inputStream, parameterType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.parameter.ParameterResolverMethod#resolveMethods
     * (java.lang.Object, java.util.List)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.returned.ReturnedResolver#resolveReturnedValue
     * (java.lang.Object, java.lang.Object, com.absir.server.on.OnPut)
     */
    @Override
    public void resolveReturnedValue(Object returnValue, Integer returned, OnPut onPut) throws Exception {
        if (returnValue != null) {
            Input input = onPut.getInput();
            input.setCharacterEncoding(charset);
            input.setContentTypeCharset(contentTypeCharset);
            OutputStream outputStream = input.getOutputStream();
            if (outputStream == null) {
                input.write(getBodyConverter(input).writeAsBytes(onPut, returnValue));

            } else {
                getBodyConverter(input).writeValue(onPut, returnValue, outputStream);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.body.IBodyConverter#getContentTypes()
     */
    @Override
    public String[] getContentTypes() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#readBodyParameterValue
     * (com.absir.server.on.OnPut, int, java.lang.String, java.lang.Class)
     */
    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType) throws Exception {
        return objectMapper.readValue(input, parameterType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#readBodyParameterValue
     * (com.absir.server.on.OnPut, int, java.io.InputStream, java.lang.Class)
     */
    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType) throws Exception {
        return objectMapper.readValue(inputStream, parameterType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#writeAsBytes(com
     * .absir.server.on.OnPut, java.lang.Object)
     */
    @Override
    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception {
        if (returnValue.getClass() == String.class) {
            return ((String) returnValue).getBytes(ContextUtils.getCharset());
        }

        return objectMapper.writeValueAsBytes(returnValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.IServerBodyConverter#writeValue(com.absir
     * .server.on.OnPut, java.lang.Object, java.io.OutputStream)
     */
    @Override
    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception {
        if (returnValue.getClass() == String.class) {
            HelperIO.write((String) returnValue, outputStream, ContextUtils.getCharset());

        } else {
            objectMapper.writeValue(outputStream, returnValue);
        }
    }

}
