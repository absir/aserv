/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:00:07
 */
package com.absir.server.route.returned;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.body.IBodyConverter;
import com.absir.server.value.Body;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Base(order = -128)
@Bean
public class ReturnedResolverBody implements ReturnedResolver<Integer> {

    public static final ReturnedResolverBody ME = BeanFactoryUtils.get(ReturnedResolverBody.class);

    protected static final String BODY_CONVERTER_NAME = ReturnedResolverBody.class + "@BODY_CONVERTER_NAME";

    protected String charset = ContextUtils.getCharset().displayName();

    @Value("server.body.contentType")
    protected String contentTypeCharset = "text/html;" + charset;

    protected Map<String, IBodyConverter> typeMapConverter;

    @Inject(type = InjectType.Selectable)
    protected void initResolver(IBodyConverter[] bodyConverters) {
        if (bodyConverters != null && bodyConverters.length > 0) {
            typeMapConverter = new HashMap<String, IBodyConverter>();
            for (IBodyConverter bodyConverter : bodyConverters) {
                String[] types = bodyConverter.getContentTypes();
                if (types != null) {
                    for (String type : types) {
                        typeMapConverter.put(type.toLowerCase(), bodyConverter);
                    }
                }
            }

            if (typeMapConverter.isEmpty()) {
                typeMapConverter = null;
            }
        }
    }

    public IBodyConverter getBodyConverter(Input input) {
        Object converter = input.getAttribute(BODY_CONVERTER_NAME);
        return converter == null || !(converter instanceof IBodyConverter) ? null : (IBodyConverter) converter;
    }

    public void setBodyConverter(Input input, IBodyConverter converter) {
        input.setAttribute(BODY_CONVERTER_NAME, converter);
    }

    public String getCharset() {
        return charset;
    }

    public String getContentTypeCharset() {
        return contentTypeCharset;
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
        if (returnValue != null) {
            Input input = onPut.getInput();
            input.setCharacterEncoding(charset);
            input.setContentTypeCharset(contentTypeCharset);
            IBodyConverter converter = getBodyConverter(input);
            if (converter == null) {
                input.write(returnValue.toString());

            } else {
                OutputStream outputStream = input.getOutputStream();
                if (outputStream == null) {
                    input.write(converter.writeAsBytes(onPut, returnValue));

                } else {
                    converter.writeValue(onPut, returnValue, outputStream);
                }
            }
        }
    }
}