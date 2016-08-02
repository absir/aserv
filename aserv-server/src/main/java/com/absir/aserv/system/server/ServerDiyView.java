/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年8月12日 下午1:56:21
 */
package com.absir.aserv.system.server;

import com.absir.aserv.support.DeveloperBreak;
import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.IRenderSuffix;
import com.absir.aserv.system.asset.Asset_diy;
import com.absir.aserv.system.helper.HelperInput;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.servlet.InputRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public abstract class ServerDiyView extends ReturnedResolverView implements IRender, IRenderSuffix {

    public static final ServerDiyView ME = BeanFactoryUtils.get(ServerDiyView.class);

    private static final String READERS_NAME = ServerDiyView.class + "@READERS_NAME";

    private static final String DONEGEN_NAME = ServerDiyView.class + "@DONEGEN_NAME";

    protected String diyExpression;

    protected String diyInclude;

    @Value("developer.diy.view")
    private String diyView = "/WEB-INF/developer/diy.html";

    public static Object[] getRenders(Input input) {
        return (Object[]) input.getAttribute(READERS_NAME);
    }

    protected String diyExpression() {
        return "<% , %>";
    }

    protected String diyInclude() {
        return echo("Pag.include(\",\")");
    }

    @Inject
    @InjectOrder(value = 255)
    protected void initDiyView() {
        if (diyExpression == null) {
            diyExpression = diyExpression();
        }

        if (diyInclude == null) {
            diyInclude = diyInclude();
        }
    }

    @Override
    public void resolveReturnedView(String view, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        if (input instanceof InputRequest) {
            render(getView(view), (InputRequest) input);

        } else {
            onPut.getInput().write(view);
        }
    }

    public void render(String view, InputRequest input) throws Exception {
        HttpServletRequest request = input.getRequest();
        request.setAttribute("INPUT", input);
        request.setAttribute("TIME", ContextUtils.getContextTime());
        Object[] renders = null;
        int diy = 0;
        if (IDeveloper.ME != null) {
            diy = IDeveloper.ME.diy(request);
            if (diy == 1) {
                Asset_diy.authentication(input);
                InModel inModel = input.getModel();
                inModel.put("diy_url", HelperInput.getRequestUrl(request));
                inModel.put("diy_view", view);
                inModel.put("diy_restore", getDiyRestore(view));
                inModel.put("diy_expression", diyExpression);
                inModel.put("diy_include", diyInclude);
                view = diyView;

            } else if (diy == 2 || IDeveloper.ME.getDeveloperNewType() != 0) {
                renders = getRenders(null, input);
                if (BeanFactoryUtils.getEnvironment() != Environment.DEVELOP) {
                    try {
                        renderView(view, renders, input);
                        return;

                    } catch (Exception e) {
                        if (!isDeveloperNotExist(e, input) || input.getAttribute(DONEGEN_NAME) != null) {
                            throw e;
                        }
                    }
                }

                input.setAttribute(DONEGEN_NAME, Boolean.TRUE);
                input.setAttribute(READERS_NAME, renders);
                IDeveloper.ME.generate(view, view, renders);
            }
        }

        renderView(view, renders, input);
    }

    protected boolean isDeveloper(Throwable e) {
        while (true) {
            if (e.getClass() == DeveloperBreak.class) {
                return true;
            }

            if ((e = e.getCause()) == null) {
                break;
            }
        }

        return false;
    }

    protected abstract boolean isNotExist(Exception e, InputRequest input);

    public boolean isDeveloperNotExist(Exception e, InputRequest inputRequest) {
        return isDeveloper(e) || isNotExist(e, inputRequest);
    }

    public String getDiyRestore(String view) throws IOException {
        if (IDeveloper.ME != null) {
            String diyView = IDeveloper.ME.getDeveloperPath(view);
            File file = new File(getRealPath(diyView));
            if (file.exists()) {
                return HelperFile.readFileToString(file);
            }

            file = new File(getRealPath(view));
            if (file.exists()) {
                return HelperFile.readFileToString(file);
            }
        }

        return "";
    }

    protected abstract String getView(String view);

    protected abstract Object getRender(String view, InputRequest input);

    protected abstract Object[] getRenders(Object render, InputRequest input);

    protected abstract void renderView(String view, Object[] renders, InputRequest input) throws Exception;
}
