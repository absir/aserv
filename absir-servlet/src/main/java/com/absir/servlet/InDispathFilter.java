/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午4:47:28
 */
package com.absir.servlet;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelDyna;
import com.absir.server.in.InDispatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class InDispathFilter extends InDispatcher<HttpServletRequest, HttpServletResponse> implements Filter {

    public static final String REQUEST_INPUT = InDispathFilter.class.getName() + "@REQUEST_INPUT";

    private static ServletContext servletContext;

    private static String contextResourcePath;

    private static String contextPath;

    private int contextPathLength;

    private String uriContextPath;

    private int uriContextPathLength;

    private boolean urlDecode;

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static String getContextResourcePath() {
        return contextResourcePath;
    }

    public static String getContextPath() {
        return contextPath;
    }

    public static final Input getInput(ServletRequest request) {
        return (Input) request.getAttribute(REQUEST_INPUT);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        contextResourcePath = servletContext.getRealPath("/");
        contextPath = filterConfig.getInitParameter("contextPath");
        if (contextPath == null) {
            BeanFactory beanFactory = BeanFactoryUtils.get();
            if (beanFactory != null) {
                contextPath = KernelDyna.to(beanFactory.getBeanConfig().getValue("contextPath"), String.class);
            }

            if (contextPath == null) {
                contextPath = filterConfig.getServletContext().getContextPath();
            }
        }

        contextPathLength = contextPath.length();
        uriContextPath = filterConfig.getInitParameter("uri");
        uriContextPathLength = uriContextPath == null ? -1 : (contextPathLength + uriContextPath.length());
        String urlDecodeing = filterConfig.getInitParameter("urlDecode");
        if (urlDecodeing == null) {
            BeanFactory beanFactory = BeanFactoryUtils.get();
            if (beanFactory != null) {
                urlDecodeing = KernelDyna.to(beanFactory.getBeanConfig().getValue("urlDecode"), String.class);
            }
        }

        if (urlDecodeing != null) {
            urlDecode = KernelDyna.to(urlDecodeing, boolean.class);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!getRouteAdapter().isStarted()) {
            response.getWriter().write("route adapter not started");
            return;
        }

        try {
            if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse && on(getUri(request),
                    (HttpServletRequest) request, (HttpServletResponse) response))) {
                chain.doFilter(request, response);
            }

        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

    private String getUri(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            String uri = ((HttpServletRequest) request).getRequestURI();
            int length = uri.length();
            if (length >= contextPathLength) {
                if (length == uriContextPathLength && uri.endsWith(uriContextPath)) {
                    String u = request.getParameter("uri");
                    return u == null ? "u" : u;
                }

                return length == contextPathLength ? "" : uri.substring(contextPathLength + 1);
            }
        }

        return request.getParameter("uri");
    }

    @Override
    public InMethod getInMethod(HttpServletRequest req) {
        if (req instanceof HttpServletRequest) {
            try {
                return InMethod.valueOf(((HttpServletRequest) req).getMethod().toUpperCase());

            } catch (Exception e) {
            }
        }

        return InMethod.GET;
    }

    @Override
    public String decodeUri(String uri, HttpServletRequest req) {
        if (urlDecode) {
            return uri;
        }

        try {
            String enc = req.getCharacterEncoding();
            if (enc == null) {
                enc = ContextUtils.getCharset().displayName();
            }

            return URLDecoder.decode(uri, enc);

        } catch (Exception e) {
            return uri;
        }
    }

    @Override
    protected Input input(String uri, InMethod inMethod, InModel model, HttpServletRequest req, HttpServletResponse res) {
        Input input = new InputRequest(uri, inMethod, model, req, res);
        req.setAttribute(REQUEST_INPUT, input);
        return input;
    }

    @Override
    public void destroy() {
    }
}
