/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-8 下午5:09:53
 */
package com.absir.servlet;

import com.absir.binder.BinderData;
import com.absir.context.core.ContextUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelLang;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class InputRequest extends Input {

    public static final ServletFileUpload SERVLET_FILE_UPLOAD_DEFAULT = new ServletFileUpload(
            new DiskFileItemFactory());

    private static final String PARAMETER_MAP_NAME = InputRequest.class.getName() + "@PARAMETER_MAP_NAME";

    private String uri;

    private InMethod method;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private String input;

    private Map<String, Object> parameterMap;

    public InputRequest(String uri, InMethod method, InModel model, HttpServletRequest request,
                        HttpServletResponse response) {
        super(model);
        if (request instanceof HttpServletRequest) {
            setId(((HttpServletRequest) request).getSession().getId());
        }

        this.uri = uri;
        this.method = method;
        this.request = request;
        this.response = response;
    }

    public static Map<String, List<FileItem>> parseParameterMap(HttpServletRequest request) {
        Object fileItems = request.getAttribute(PARAMETER_MAP_NAME);
        if (fileItems == null || !(fileItems instanceof Map)) {
            try {
                fileItems = SERVLET_FILE_UPLOAD_DEFAULT.parseParameterMap(request);

            } catch (Exception e) {
                fileItems = KernelLang.NULL_MAP;
            }

            request.setAttribute(PARAMETER_MAP_NAME, fileItems);
        }

        return (Map<String, List<FileItem>>) fileItems;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public BinderData getBinderData() {
        if (binderData == null) {
            binderData = new BinderRequest();
        }

        return binderData;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public InMethod getMethod() {
        return method;
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public boolean paramDebug() {
        return request.getParameter("DEBUG") != null;
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        request.setAttribute(name, obj);
    }

    @Override
    public String getParam(String name) {
        return request.getParameter(name);
    }

    @Override
    public String[] getParams(String name) {
        Object values = getParamMap().get(name);
        return values == null || !(values instanceof String[]) ? null : (String[]) values;
    }

    @Override
    public Map<String, Object> getParamMap() {
        if (parameterMap == null) {
            if (method != InMethod.GET) {
                String contentType = request.getContentType();
                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    Map<String, List<FileItem>> fileItems = parseParameterMap(request);
                    if (!fileItems.isEmpty()) {
                        parameterMap = new LinkedHashMap<String, Object>();
                        for (Entry<String, List<FileItem>> entry : fileItems.entrySet()) {
                            List<Object> parameters = null;
                            boolean fileItem = false;
                            for (FileItem item : entry.getValue()) {
                                if (parameters == null) {
                                    parameters = new ArrayList<Object>();
                                }

                                if (item.isFormField()) {
                                    parameters.add(new String(item.get(), ContextUtils.getCharset()));

                                } else {
                                    fileItem = true;
                                    parameters.add(item);
                                }
                            }

                            if (parameters != null) {
                                parameterMap.put(entry.getKey(),
                                        DynaBinder.to(parameters, fileItem ? Object[].class : String[].class));
                            }
                        }

                        return parameterMap;
                    }
                }
            }

            parameterMap = (Map<String, Object>) (Object) KernelLang.NULL_MAP;
        }

        return (Object) parameterMap == KernelLang.NULL_MAP ? request.getParameterMap() : parameterMap;
    }

    public Map<String, List<FileItem>> parseParameterMap() {
        return parseParameterMap(request);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public String getInput() {
        if (input == null) {
            try {
                input = HelperIO.toString(getInputStream(), KernelCharset.getDefault());

            } catch (IOException e) {
                input = KernelLang.NULL_STRING;
            }
        }

        return input;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }

    @Override
    public void setContentTypeCharset(String contentTypeCharset) {
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse) response).setHeader("content-type", contentTypeCharset);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        getOutputStream().write(b, off, len);
    }

    public Object getSession(String name) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return session.getAttribute(name);
    }

    public String getSessionValue(String name) {
        Object value = getSession(name);
        return value == null ? null : value.toString();
    }

    public void setSession(String name, Object value) {
        request.getSession().setAttribute(name, value);
    }

    public void removeSession(String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.getSession().removeAttribute(name);
        }
    }

    public String getCookie(String name) {
        Cookie cookie = InputCookies.getCookie(request, name);
        return cookie == null ? null : cookie.getValue();
    }

    public void setCookie(String name, String value, String path, long remember) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge((int) (remember / 1000));
        response.addCookie(cookie);
    }

    public void removeCookie(String name, String path) {
        setCookie(name, null, path, 0);
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }
}
