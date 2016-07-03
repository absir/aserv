/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.support.web;

import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.IRenderSuffix;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelLang;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.servlet.InDispathFilter;
import com.absir.servlet.InputRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Base(order = -1)
@Bean
public class WebJstlView extends ReturnedResolverView implements IRender, IRenderSuffix {

    public static final String LAYOUT_NAME = "layout";

    public static final String LAYOUT_BODY_NAME = "layout_body";

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebJstlView.class);

    private static final int LAYOUT_ITERATE_DEPTH = 6;

    private static final String PRERPARE_NAME = "prerpare";

    private static final Map<String, String> File_Name_Map_Layout_Name = new HashMap<String, String>();

    private static String Layout_Name_View = LAYOUT_NAME + ".jsp";

    private static String Server_Context_Path = null;

    @Value("web.view.prefix")
    private String prefix = "/WEB-INF/jsp/";

    @Value("web.view.suffix")
    private String suffix = ".jsp";

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void resolveReturnedView(String view, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        if (input instanceof InputRequest) {
            InputRequest inputRequest = (InputRequest) input;
            renderView(prefix + view + suffix, input, inputRequest.getRequest(), inputRequest.getResponse());

        } else {
            onPut.getInput().write(view);
        }
    }

    public void renderView(String view, Input input, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding(ContextUtils.getCharset().displayName());
        request.setAttribute(PRERPARE_NAME, view);
        for (Entry<String, Object> entry : input.getModel().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        renderMergeOutputLayout(view, request, response, new WebResponseWrapper(response), LAYOUT_ITERATE_DEPTH);
    }

    private String getLayoutFilename(String filename) {
        if (Server_Context_Path == null) {
            Server_Context_Path = InDispathFilter.getServletContext().getRealPath("");
        }

        String layoutFilename = File_Name_Map_Layout_Name.get(filename);
        if (layoutFilename == null) {
            synchronized (this) {
                layoutFilename = File_Name_Map_Layout_Name.get(filename);
                if (layoutFilename == null) {
                    layoutFilename = Server_Context_Path + HelperFileName.addFilenameSubExtension(filename, LAYOUT_NAME);
                    if (!HelperFile.fileExists(layoutFilename)) {
                        layoutFilename = HelperFileName.iterateFilename(Server_Context_Path, filename, Layout_Name_View);
                    }

                    if (layoutFilename == null) {
                        layoutFilename = KernelLang.NULL_STRING;

                    } else {
                        layoutFilename = layoutFilename.substring(Server_Context_Path.length());
                    }

                    File_Name_Map_Layout_Name.put(filename, layoutFilename);
                }
            }
        }

        return layoutFilename;
    }

    protected void renderMergeOutputLayout(String view, HttpServletRequest request, HttpServletResponse response, WebResponseWrapper wrapper, int depth) throws Exception {
        RequestDispatcher rd = request.getRequestDispatcher(view);
        rd.include(request, wrapper);
        String content = wrapper.getContent();
        if (depth-- != 0) {
            Object layout = request.getAttribute(LAYOUT_NAME);
            if (layout != null) {
                if (layout.getClass() == Boolean.class) {
                    if ((Boolean) layout) {
                        Object filename = request.getAttribute(PRERPARE_NAME);
                        if (filename != null) {
                            if (filename instanceof String) {
                                layout = getLayoutFilename((String) filename);
                            }
                        }
                    }
                }

                request.removeAttribute(LAYOUT_NAME);
                if (KernelLang.NULL_STRING != layout && layout instanceof String) {
                    request.setAttribute(PRERPARE_NAME, layout);
                    request.setAttribute(LAYOUT_BODY_NAME, content);
                    content = null;
                    wrapper.resetBuffer();
                    renderMergeOutputLayout((String) layout, request, response, wrapper, depth);
                }
            }
        }

        if (content != null) {
            response.getWriter().append(content);
        }
    }

    @Override
    public String dev(long devTime) {
        return "<% Pag.dev(" + devTime + "); %>";
    }

    @Override
    public String echo(String value) {
        return "<%=" + value + "%>";
    }

    @Override
    public String include(String path) {
        return "<jsp:include page=\"" + path + "\"/>";
    }

    @Override
    public void include(String path, Object... renders) throws IOException {
        try {
            WebJsplUtils.include(path, (PageContext) renders[0], (HttpServletRequest) renders[1], (HttpServletResponse) renders[2]);

        } catch (ServletException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getPath(Object... renders) throws IOException {
        return WebJsplUtils.getServletPath((PageContext) renders[0]);
    }

    @Override
    public String getFullPath(String path, Object... renders) throws IOException {
        return WebJsplUtils.getFullIncludePath(path, (PageContext) renders[0]);
    }

    @Override
    public String getRealPath(String path, Object... renders) throws IOException {
        return InDispathFilter.getServletContext().getRealPath(path);
    }

    @Override
    public void rend(OutputStream outputStream, String path, Object... renders) throws IOException {
        try {
            WebJsplUtils.render(outputStream, path, (HttpServletRequest) renders[1], (HttpServletResponse) renders[2]);

        } catch (ServletException e) {
            throw new IOException(e);
        }
    }
}
