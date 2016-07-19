/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午1:06:37
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.helper.HelperInput;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.server.ServerResolverRedirect;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Value;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.IRoute;
import com.absir.server.route.RouteAction;
import com.absir.server.route.RouteMapping;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.server.value.*;
import org.hibernate.exception.ConstraintViolationException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;

@Interceptors(AdminServer.Route.class)
public abstract class AdminServer {

    @Value("admin.login.remember")
    protected static boolean remember = true;

    @Value("admin.login.level")
    protected static int roleLevel = JeRoleLevel.ROLE_ADMIN.ordinal();

    private static String route = "admin";

    public static String getRoute() {
        return route;
    }

    @Before
    protected SecurityContext onAuthentication(Input input) throws Exception {
        SecurityContext securityContext = SecurityService.ME.autoLogin("admin", true, JeRoleLevel.ROLE_ADMIN.ordinal(), input);
        if (securityContext == null || !securityContext.getUser().isActivation()) {
            ServerResolverRedirect.redirect(MenuContextUtils.getAdminRoute() + "login?redirect=/" + input.getUri(), false, input);
        }

        return securityContext;
    }

    @After
    protected void onView(OnPut onPut) {
        if (onPut.getReturnedResolver() == null) {
            RouteAction routeAction = onPut.getInput().getRouteAction();
            if (routeAction != null) {
                Class<?> returnType = routeAction.getRouteMethod().getMethod().getReturnType();
                if (returnType == void.class || returnType == String.class) {
                    if (returnType == void.class || onPut.getReturnValue() == null) {
                        String routeView = routeAction.getRouteView();
                        if (routeView == null) {
                            routeView = HelperFileName.normalizeNoEndSeparator(new String(onPut.getInput().getRouteMatcher().getMapping()));
                            routeView = routeView.replaceFirst(route, "admin");
                            routeAction.setRouteView(routeView);
                        }

                        onPut.setReturnValue(routeView);
                    }

                    onPut.setReturnedResolver(ReturnedResolverView.ME);
                }
            }
        }
    }

    @OnException(Exception.class)
    protected void onException(Exception e, OnPut onPut) {
        Input input = onPut.getInput();
        if (HelperInput.isAjax(input)) {
            if (e instanceof ConstraintViolationException) {
                input.getModel().put("message", e.getCause().getMessage());
            }

            ReturnedResolverView.setReturnView(onPut, "admin/exception");
        }
    }

    public static class Route extends TransactionIntercepter implements IRoute {

        @Inject(type = InjectType.Selectable)
        private void setRoute(@Value(value = "admin.route") String route) {
            AdminServer.route = route;
        }

        @Override
        public void routeMapping(String name, Entry<Mapping, List<String>> mapping, Method method, List<String> parameterPathNames, List<String> mappings, List<InMethod> inMethods) {
            RouteMapping.routeMapping(AdminServer.route, name, mapping, method, method.getName(), KernelString.implode(KernelArray.repeat('*', parameterPathNames.size()), '/'),
                    KernelCollection.toArray(parameterPathNames, String.class), mappings, inMethods);
        }
    }
}
