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
import com.absir.server.in.Input;
import com.absir.servlet.InputRequest;
import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.template.JetTemplate;
import jetbrick.template.runtime.InterpretContext;
import jetbrick.template.web.JetWebContext;
import jetbrick.util.PathUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Bean
public class WebJetbrickView extends ServerDiyView implements IPagLang {

    @Value("web.view.prefix")
    private String prefix = "/WEB-INF/tpl/";

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

    protected String diyExpression() {
        return "#";
    }

    protected String diyInclude() {
        return echo("Pag.include(\",\")");
    }

    @Override
    protected boolean isNotExist(Exception e, Input input) {
        return e.getClass() == ResourceNotFoundException.class || e.getMessage().startsWith("include file not found");
    }

    @Override
    public String dev(int devTime) {
        return "${Pag::devI(" + devTime + ")}";
    }

    @Override
    public String echo(String value) {
        return "${" + value + '}';
    }

    @Override
    public String include(String path) {
        return "#include(\"" + path + "\")\r\n";
    }

    @Override
    public void include(String path, Object... renders) throws IOException {
        InterpretContext context = InterpretContext.current();
        context.doIncludeCall(path, null, null);
    }

    @Override
    public String getPath(Object... renders) throws IOException {
        InterpretContext context = InterpretContext.current();
        return context.getTemplate().getName();
    }

    @Override
    public String getFullPath(String path, Object... renders) throws IOException {
        return getFullPath(InterpretContext.current().getTemplate(), path);
    }

    @Override
    public String getRealPath(String path, Object... renders) throws IOException {
        return WebJetbrickSupply.getResourceLoaderRoot() + path;
    }

    @Override
    public void rend(OutputStream outputStream, String path, Object... renders) throws IOException {
        Map<String, Object> context = (Map<String, Object>) renders[0];
        WebJetbrickSupply.getEngine().getTemplate(path).render(context, outputStream);
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    protected String getView(String view) {
        return prefix + view + getSuffix();
    }

    @Override
    protected Object getRender(String view, InputRequest input) {
        return null;
    }

    protected JetWebContext createWebContext(InputRequest input) {
        JetWebContext context = new JetWebContext(input.getRequest(), input.getResponse(), input.getModel());
        return context;
    }

    @Override
    protected Object[] getRenders(Object render, InputRequest input) {
        return new Object[]{render == null ? createWebContext(input) : render, input.getRequest()};
    }

    @Override
    protected void renderView(String view, Object[] renders, InputRequest input) throws Exception {
        Map<String, Object> context = renders == null ? createWebContext(input) : (Map<String, Object>) renders[0];
        WebJetbrickSupply.getEngine().getTemplate(view).render(context, input.getResponse().getOutputStream());
    }

    @Override
    public String getPagLang(String transferredName) {
        return "Pag::lang(" + transferredName + ")";
    }
}
