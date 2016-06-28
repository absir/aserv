/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午7:36:47
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.server.value.Redirect;
import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanConfig;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.server.in.InModel;
import com.absir.server.on.OnPut;
import com.absir.server.value.Server;

import java.net.InetAddress;
import java.util.Properties;

@Base
@Server
public class Admin_route extends AdminServer {

    /**
     * 默认跳转首页
     *
     * @param onPut
     * @return
     */
    @Redirect
    public String route(OnPut onPut) {
        return MenuContextUtils.getAdminRoute() + "main";
    }

    /**
     * 管理首页
     *
     * @param model
     */
    public void main(InModel model) {
    }

    /**
     * 欢迎首页
     *
     * @param model
     */
    public void welcome(InModel model) {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress().toString();
            String hostName = addr.getHostName().toString();
            model.put("hostName", hostName);
            model.put("ip", ip);

        } catch (Exception e) {
            Environment.throwable(e);
        }

        model.put("osTime", System.currentTimeMillis());
        model.put("contextTime", ContextUtils.getContextTime());

        Properties properties = System.getProperties();
        model.put("osName", properties.get("os.name"));
        model.put("osArch", properties.get("os.arch"));
        model.put("osVersion", properties.get("os.version"));
        model.put("userHome", properties.get("user.home"));
        model.put("javaVersion", properties.get("java.version"));
        model.put("javaVendor", properties.get("java.vendor"));
        model.put("javaHome", properties.get("java.home"));

        BeanConfig config = BeanFactoryUtils.getBeanConfig();
        model.put("classPath", config.getClassPath());
        model.put("resourcePath", config.getResourcePath());
    }
}
