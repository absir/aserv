/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月1日 下午12:14:05
 */
package com.absir.aserv.support.web;

import com.absir.aserv.developer.Pag.IPagLang;
import com.absir.aserv.system.server.ServerDiyView;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.servlet.InputRequest;
import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.template.JetTemplate;
import jetbrick.template.runtime.InterpretContext;
import jetbrick.template.web.JetWebContext;
import jetbrick.util.PathUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author absir
 */
@Bean
public class WebJetbrickView extends ServerDiyView implements IPagLang {

    /**
     * prefix
     */
    @Value("web.view.prefix")
    private String prefix = "/WEB-INF/tpl/";
    /**
     * suffix
     */
    @Value("web.view.suffix")
    private String suffix = ".jetx";

    /**
     * 获取文件完整路径
     *
     * @param template
     * @param path
     * @return
     */
    public static String getFullPath(JetTemplate template, String path) {
        return PathUtils.getRelativePath(template.getName(), path);
    }

    /**
     * @return
     */
    protected String diyExpression() {
        return "#";
    }

    /**
     * @return
     */
    protected String diyInclude() {
        return echo("Pag.include(\",\")");
    }

    /**
     * @param e
     * @param input
     * @return
     */
    @Override
    protected boolean isNotExist(Exception e, InputRequest input) {
        return e.getClass() == ResourceNotFoundException.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.support.developer.IRender#echo(java.lang.String)
     */
    @Override
    public String echo(String value) {
        return "${" + value + '}';
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#include(java.lang.String)
     */
    @Override
    public String include(String path) {
        return "#include(\"" + path + "\")\r\n";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#include(java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void include(String path, Object... renders) throws IOException {
        InterpretContext context = InterpretContext.current();
        context.doIncludeCall(path, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#getPath(java.lang.Object[])
     */
    @Override
    public String getPath(Object... renders) throws IOException {
        InterpretContext context = InterpretContext.current();
        return context.getTemplate().getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#getFullPath(java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public String getFullPath(String path, Object... renders) throws IOException {
        return getFullPath(InterpretContext.current().getTemplate(), path);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#getRealPath(java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public String getRealPath(String path, Object... renders) throws IOException {
        return WebJetbrickSupply.getResourceLoaderRoot() + path;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.support.developer.IRender#rend(java.io.OutputStream,
     * java.lang.String, java.lang.Object[])
     */
    @Override
    public void rend(OutputStream outputStream, String path, Object... renders) throws IOException {
        Map<String, Object> context = (Map<String, Object>) renders[0];
        WebJetbrickSupply.getEngine().getTemplate(path).render(context, outputStream);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.support.developer.IRenderSuffix#getSuffix()
     */
    @Override
    public String getSuffix() {
        return suffix;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.ServerDiyView#getView(java.lang.String)
     */
    @Override
    protected String getView(String view) {
        return prefix + view + getSuffix();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.ServerDiyView#getRender(java.lang.String,
     * com.absir.servlet.InputRequest)
     */
    @Override
    protected Object getRender(String view, InputRequest input) {
        return null;
    }

    /**
     * @param input
     * @return
     */
    protected JetWebContext createWebContext(InputRequest input) {
        JetWebContext context = new JetWebContext(input.getRequest(), input.getResponse(), input.getModel());
        return context;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.ServerDiyView#getRenders(java.lang.Object
     * , com.absir.servlet.InputRequest)
     */
    @Override
    protected Object[] getRenders(Object render, InputRequest input) {
        return new Object[]{render == null ? createWebContext(input) : render, input.getRequest()};
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.server.ServerDiyView#renderView(java.lang.String
     * , java.lang.Object[], com.absir.servlet.InputRequest)
     */
    @Override
    protected void renderView(String view, Object[] renders, InputRequest input) throws Exception {
        Map<String, Object> context = renders == null ? createWebContext(input) : (Map<String, Object>) renders[0];
        WebJetbrickSupply.getEngine().getTemplate(view).render(context, input.getResponse().getOutputStream());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.developer.Pag.IPagLang#getPagLang(java.lang.String)
     */
    @Override
    public String getPagLang(String transferredName) {
        return "Pag::lang(" + transferredName + ")";
    }
}
