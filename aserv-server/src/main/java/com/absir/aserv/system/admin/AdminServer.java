/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-6 下午1:06:37
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.helper.HelperInput;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.server.ServerResolverRedirect;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Value;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.IRoute;
import com.absir.server.route.RouteAction;
import com.absir.server.route.RouteMapping;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;
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
            ServerResolverRedirect.redirect(MenuContextUtils.getAdminRoute() + "login?redirect=" + MenuContextUtils.getSiteRoute() + input.getUri(), false, input);
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
    protected void onException(Exception e, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        InModel model = input.getModel();
        Throwable throwable = UtilAbsir.forCauseThrowable(e);
        model.put("e", throwable);
        if (throwable instanceof ConstraintViolationException) {
            String message = throwable.getCause().getMessage();
            model.put("message", throwable.getCause().getMessage());

        } else {
            if (throwable.getClass() == ServerException.class) {
                model.put("message", ((ServerException) throwable).getServerStatus() + " : " + onPut.getInput().getBinderDataErrors());

            } else {
                throw e;
            }
        }

        if (HelperInput.isAjax(input)) {
            onPut.setReturnedResolver(ReturnedResolverView.ME, "admin/exception.ajax");

        } else {
            onPut.setReturnedResolver(ReturnedResolverView.ME, "admin/exception");
        }
    }

    /**
     * CRUDSupply统一入口
     */
    protected ICrudSupply getCrudSupply(String entityName, Input input) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        if (crudSupply == null) {
            throw new ServerException(ServerStatus.IN_404);
        }

        if (input != null) {
            JoEntity joEntity = new JoEntity(entityName, crudSupply.getEntityClass(entityName));
            input.setAttribute("joEntity", joEntity);
        }

        return crudSupply;
    }

    /**
     * 选择授权
     */
    protected void suggest(String entityName, ICrudSupply crudSupply, Input input) {
        if (SessionFactoryUtils.entityPermission(entityName, JePermission.SUGGEST) || AuthServiceUtils.suggestPermission(entityName, SecurityService.ME.getUserBase(input))) {
            return;
        }

        if (crudSupply instanceof CrudSupply || !(input instanceof InputRequest)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        if (((InputRequest) input).getSession(EntityStatics.suggestKey(entityName)) == null) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }
    }

    public static class Route extends TransactionIntercepter implements IRoute {

        @Inject(type = InjectType.Selectable)
        private void setRoute(@Value(value = "admin.route") String route) {
            AdminServer.route = route;
        }

        @Override
        public void routeMapping(String name, Entry<Mapping, List<String>> mapping, Method method, List<String> parameterPathNames, List<String> mappings, List<InMethod> inMethods) {
            RouteMapping.routeMapping(!KernelString.isEmpty(name) && name.equals("open") ? null : AdminServer.route, name, mapping, method, method.getName(), KernelString.implode(KernelArray.repeat('*', parameterPathNames.size()), '/'),
                    KernelCollection.toArray(parameterPathNames, String.class), mappings, inMethods);
        }
    }
}
