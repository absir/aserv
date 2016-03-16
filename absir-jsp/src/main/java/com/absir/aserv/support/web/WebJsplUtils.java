/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午8:17:42
 */
package com.absir.aserv.support.web;

import com.absir.aserv.developer.Pag;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.*;
import java.net.MalformedURLException;

public class WebJsplUtils extends Pag {

    public static String getServletPath(ServletRequest request) {
        Object requestDispatcherPath = KernelObject.declaredGet(request, "requestDispatcherPath");
        if (requestDispatcherPath != null) {
            return requestDispatcherPath.toString();
        }

        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getServletPath();
        }

        return null;
    }

    public static String getServletPath(PageContext pageContext) {
        return getServletPath(pageContext.getRequest());
    }

    public static String getFullIncludePath(String includePath, PageContext pageContext) {
        if (!includePath.startsWith("/")) {
            String servletPath = getServletPath(pageContext);
            if (servletPath != null) {
                includePath = servletPath.substring(0, servletPath.lastIndexOf('/')) + '/' + includePath;
            }
        }

        return includePath;
    }

    public static String getFullExistIncludePath(String includePath, PageContext pageContext) {
        includePath = getFullIncludePath(includePath, pageContext);
        try {
            if (pageContext.getServletContext().getResource(includePath) != null) {
                return includePath;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void include(String includePath, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(includePath);
        rd.forward(request, response);
    }

    public static String getIncludeContent(String includePath, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        WebResponseWrapper render = new WebResponseWrapper(response);
        request.getRequestDispatcher(includePath).include(request, render);
        return render.getContent();
    }

    public static void include(String includePath, PageContext pageContext, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        pageContext.getOut().print(getIncludeContent(includePath, request, response));
    }

    public static boolean includeExist(String includePath, PageContext pageContext, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        includePath = getFullExistIncludePath(includePath, pageContext);
        if (includePath != null) {
            include(includePath, pageContext, request, response);
            return true;
        }

        return false;
    }

    public static void render(OutputStream output, String includeContent, ServletResponse response) throws UnsupportedEncodingException, IOException {
        output.write(includeContent.getBytes(response.getCharacterEncoding()));
    }

    public static void render(OutputStream output, String includePath, ServletRequest request, ServletResponse response) throws UnsupportedEncodingException, IOException, ServletException {
        render(output, getIncludeContent(includePath, request, response), response);
    }

    public static void render(File file, String includePath, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        FileOutputStream outputStream = HelperFile.openOutputStream(file);
        try {
            render(outputStream, getIncludeContent(includePath, request, response), response);

        } finally {
            outputStream.close();
        }
    }

    public static void render(String filepath, String includePath, HttpServletRequest request, ServletResponse response) throws ServletException, IOException {
        filepath = request.getSession().getServletContext().getRealPath(filepath);
        render(new File(filepath), includePath, request, response);
    }

    public static void render(File file, Long lastModified, String includePath, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        FileOutputStream output = null;
        try {
            output = HelperFile.openOutputStream(file, lastModified);
            if (output != null) {
                render(output, getIncludeContent(includePath, request, response), response);
            }

        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public static void render(String filepath, Long lastModified, String includePath, HttpServletRequest request, ServletResponse response) throws ServletException, IOException {
        filepath = request.getSession().getServletContext().getRealPath(filepath);
        render(new File(filepath), lastModified, includePath, request, response);
    }

    public static void renderInclude(String filepath, String includePath, PageContext pageContext, HttpServletRequest request, ServletResponse response) throws ServletException, IOException {
        filepath = getFullIncludePath(filepath, pageContext);
        render(filepath, includePath, request, response);
        include(filepath, pageContext, request, response);
    }

    public static void renderInclude(String filepath, Long lastModified, String includePath, PageContext pageContext, HttpServletRequest request, ServletResponse response) throws ServletException,
            IOException {
        filepath = getFullIncludePath(filepath, pageContext);
        render(filepath, lastModified, includePath, request, response);
        include(filepath, pageContext, request, response);
    }
}
