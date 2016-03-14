/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-18 下午5:40:49
 */
package com.absir.server.in;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.binder.BinderData;
import com.absir.context.core.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.context.lang.LangBundle;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteAction;
import com.absir.server.route.RouteEntry;
import com.absir.server.route.RouteMatcher;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.route.returned.ReturnedResolverView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
@Inject
public abstract class Input extends Bean<Serializable> implements IAttributes {

    /**
     * GET
     */
    public final static IGet GET = BeanFactoryUtils.get(IGet.class);
    /**
     * resourceBundle
     */
    protected Map<String, String> resourceBundle;
    /**
     * binderData
     */
    protected BinderData binderData;
    /**
     * model
     */
    private InModel model;
    /**
     * locale
     */
    private Locale locale;
    /**
     * localCode
     */
    private Integer localCode;
    /**
     * dispatcher
     */
    private IDispatcher dispatcher;
    /**
     * routeMatcher
     */
    private RouteMatcher routeMatcher;

    /**
     * @param model
     */
    public Input(InModel model) {
        this.model = model;
    }

    /**
     * @return the model
     */
    public InModel getModel() {
        return model;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        if (locale == null) {
            locale = LangBundle.ME.getLocale(getLocalCode());
        }

        return locale;
    }

    /**
     * @return
     */
    public Integer getLocalCode() {
        if (localCode == null) {
            localCode = GET == null ? null : GET.getLocaleCode(this);
            if (localCode == null) {
                localCode = 0;
            }
        }

        return localCode;
    }

    /**
     * @param code
     */
    public void setLocaleCode(Integer code) {
        locale = LangBundle.ME.getLocale(code);
        if (locale == LangBundle.ME.getLocale()) {
            code = 0;
        }

        localCode = code;
    }

    /**
     * @param lang
     * @return
     */
    public String getLang(String lang) {
        Locale locale = getLocale();
        if (resourceBundle == null) {
            resourceBundle = LangBundle.ME.getResourceBundle(locale);
        }

        return LangBundle.ME.getLangResource(lang, resourceBundle, locale);
    }

    /**
     * @param lang
     * @return
     */
    public String getLangValue(String lang) {
        return getLangValue(lang, lang);
    }

    /**
     * @param lang
     * @param value
     * @return
     */
    public String getLangValue(String lang, String value) {
        LangBundle.ME.getResourceBundle().put(lang, value);
        return getLang(lang);
    }

    /**
     * @param name
     * @param beanName
     * @param toClass
     * @return
     */
    public <T> T get(String name, String beanName, Class<T> toClass) {
        return BeanConfigImpl.getMapValue(model, name, beanName, toClass);
    }

    /**
     * @param name
     * @param beanName
     * @param toType
     * @return
     */
    public Object get(String name, String beanName, Type toType) {
        return BeanConfigImpl.getMapValue(model, name, beanName, toType);
    }

    /**
     * @return the dispatcher
     */
    public IDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * @param dispatcher the dispatcher to set
     */
    public void setDispatcher(IDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * @return the routeMatcher
     */
    public RouteMatcher getRouteMatcher() {
        return routeMatcher;
    }

    /**
     * @param routeMatcher the routeMatcher to set
     */
    public void setRouteMatcher(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    /**
     * @return
     */
    public RouteAction getRouteAction() {
        return routeMatcher == null ? null : routeMatcher.getRouteAction();
    }

    /**
     * @return
     */
    public RouteEntry getRouteEntry() {
        RouteAction routeAction = getRouteAction();
        return routeAction == null ? null : routeAction.getRouteEntry();
    }

    /**
     * @param iterator
     * @return
     * @throws Throwable
     */
    public OnPut intercept(Iterator<Interceptor> iterator) throws Throwable {
        RouteEntry routeEntry = getRouteEntry();
        return routeEntry == null ? null : routeEntry.intercept(iterator, this);
    }

    /**
     * @return
     */
    public BinderData getBinderData() {
        if (binderData == null) {
            binderData = new BinderData();
        }

        return binderData;
    }

    /**
     * @return
     */
    protected Locale getLocaled() {
        return LangBundle.ME.getLocale();
    }

    /**
     * @return
     */
    public abstract String getUri();

    /**
     * @return
     */
    public abstract InMethod getMethod();

    /**
     * @param status
     */
    public abstract void setStatus(int status);

    /**
     * @return
     */
    public boolean isDebug() {
        return BeanFactoryUtils.getEnvironment() != Environment.PRODUCT && paramDebug();
    }

    /**
     * @return
     */
    public abstract boolean paramDebug();

    /**
     * @return
     */
    public abstract String getAddress();

    /**
     * @param name
     * @return
     */
    public abstract String getParam(String name);

    /**
     * @param name
     * @return
     */
    public abstract String[] getParams(String name);

    /**
     * @return
     */
    public abstract Map<String, Object> getParamMap();

    /**
     * @return
     * @throws IOException
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * @return
     */
    public abstract String getInput();

    /**
     * @param charset
     */
    public abstract void setCharacterEncoding(String charset);

    /**
     * @param contentType
     * @param charset
     */
    public void setContentTypeCharset(String contentType, String charset) {
        setCharacterEncoding(charset);
        setContentTypeCharset(contentType + ";" + charset);
    }

    public abstract void setContentTypeCharset(String contentTypeCharset);

    /**
     * @return
     * @throws IOException
     */
    public abstract OutputStream getOutputStream() throws IOException;

    /**
     * @param string
     * @throws IOException
     */
    public void write(String string) throws IOException {
        OutputStream outputStream = getOutputStream();
        if (outputStream == null) {
            write(string.getBytes(ContextUtils.getCharset()));

        } else {
            HelperIO.write(string, outputStream);
        }
    }

    /**
     * @param b
     * @throws IOException
     */
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public abstract void write(byte b[], int off, int len) throws IOException;

    /**
     * @param onPut
     * @return
     */
    public ReturnedResolver<?> getReturnedResolver(OnPut onPut) {
        return ReturnedResolverView.ME;
    }
}
