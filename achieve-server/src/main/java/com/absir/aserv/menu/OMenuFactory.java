/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-6 下午12:42:34
 */
package com.absir.aserv.menu;

import com.absir.aop.*;
import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.menu.OMenuFactory.MenuAopInterceptor;
import com.absir.aserv.menu.value.MaFactory;
import com.absir.aserv.menu.value.MaPermission;
import com.absir.aserv.menu.value.MaPermissionRef;
import com.absir.aserv.system.bean.JMenuPermission;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.aserv.system.service.AuthService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteDefine;
import com.absir.server.route.RouteFactory;
import com.absir.server.route.RouteMatcher;
import com.absir.server.route.parameter.ParameterResolverPath;
import net.sf.cglib.proxy.MethodProxy;
import org.hibernate.Session;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
@Bean
@Basis
public class OMenuFactory extends AopMethodDefineAbstract<MenuAopInterceptor, String, String> implements IMenuFactory {

    /**
     * permissions
     */
    private Map<String, String> permissions = new HashMap<String, String>();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aop.AopMethodDefine#getAopInterceptor(com.absir.bean.basis.
     * BeanDefine, java.lang.Object)
     */
    @Override
    public MenuAopInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
        return BeanFactoryImpl.getBeanDefine(beanDefine, RouteDefine.class) == null ? null : new MenuAopInterceptor();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public String getAopInterceptor(String variable, Class<?> beanType) {
        MaPermission maPermission = beanType.getAnnotation(MaPermission.class);
        if (maPermission == null) {
            return null;
        }

        String value = maPermission.value();
        if (KernelString.isEmpty(value)) {
            return null;
        }

        if (!permissions.containsKey(value) || !KernelString.isEmpty(maPermission.name())) {
            permissions.put(value, maPermission.name());
        }

        if (beanType.getAnnotation(MaPermissionRef.class) == null) {
            return value;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
     * java.lang.Object, java.lang.Class, java.lang.reflect.Method)
     */
    @Override
    public String getAopInterceptor(String interceptor, String variable, Class<?> beanType, Method method) {
        MaPermission maPermission = method.getAnnotation(MaPermission.class);
        if (maPermission == null) {
            if (!RouteFactory.isMethodServering(method)) {
                return null;
            }

        } else {
            interceptor = maPermission.value();
        }

        if (KernelString.isEmpty(interceptor)) {
            return null;
        }

        if (maPermission != null) {
            if (!permissions.containsKey(interceptor) || !KernelString.isEmpty(maPermission.name())) {
                permissions.put(interceptor, maPermission.name());
            }
        }

        return interceptor;
    }

    /**
     * 添加菜单默认权限
     */
    @Transaction
    @Started
    protected void initPermissions() {
        if (permissions != null) {
            Session session = BeanDao.getSession();
            for (Entry<String, String> entry : permissions.entrySet()) {
                String ref = entry.getKey();
                JMenuPermission menuPermission = (JMenuPermission) QueryDaoUtils.select(session, "JMenuPermission", new Object[]{"o.id", ref});
                if (menuPermission == null) {
                    menuPermission = new JMenuPermission();
                    menuPermission.setId(ref);
                    String caption = entry.getValue();
                    menuPermission.setCaption(KernelString.isEmpty(caption) ? ref : caption);
                    menuPermission.setAllowIds(new long[]{1L});
                    session.persist(menuPermission);
                }
            }

            permissions = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.feature.menu.IMenuFactory#proccess(java.lang.String,
     * com.absir.aserv.feature.menu.MenuBeanRoot,
     * com.absir.server.route.RouteMatcher,
     * com.absir.aserv.feature.menu.value.MaFactory)
     */
    @Override
    public void proccess(String route, MenuBeanRoot menuBeanRoot, RouteMatcher routeMatcher, MaFactory maFactory) {
        if (KernelArray.getAssignable(routeMatcher.getRouteAction().getRouteMethod().getParameterResolvers(), ParameterResolverPath.class) == null) {
            String ref = null;
            Object routeObject = routeMatcher.getRouteAction().getRouteEntity().getRoute();
            Method method = routeMatcher.getRouteAction().getRouteMethod().getMethod();
            if (routeObject instanceof AopProxy) {
                MenuAopInterceptor menuAopInterceptor = KernelCollection.getAssignable(((AopProxy) routeObject).getAopInterceptors(), MenuAopInterceptor.class);
                if (menuAopInterceptor != null) {
                    ref = menuAopInterceptor.getMethodMapInterceptor().get(method);
                }
            }

            int offset = 0;
            int length = routeMatcher.getMapping().length;
            if (route != null) {
                offset = route.length();
                length -= offset;
            }

            Class<?> entityClass = AopProxyUtils.getBeanType(routeObject);
            MenuContextUtils.addMenuBeanRoot(menuBeanRoot, null, entityClass, LangBundleImpl.ME.getunLang("功能管理", MenuBeanRoot.TAG), "briefcase", 1, ref, '/' + new String(routeMatcher.getMapping(),
                    offset, length, ContextUtils.getCharset()), HelperLang.getMethodCaption(method, entityClass), null, null, maFactory.parent(), maFactory.menu());
        }
    }

    /**
     * @author absir
     */
    public static class MenuAopInterceptor extends AopInterceptorAbstract<String> {

		/*
         * (non-Javadoc)
		 *
		 * @see com.absir.aop.AopInterceptor#before(java.lang.Object,
		 * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
		 * java.lang.reflect.Method, java.lang.Object[],
		 * net.sf.cglib.proxy.MethodProxy)
		 */

        @Override
        public Object before(Object proxy, Iterator<AopInterceptor> iterator, String interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            OnPut onPut = OnPut.get();
            JiUserBase user = onPut == null ? null : SecurityService.ME.getUserBase(onPut.getInput());
            if (!AuthService.ME.menuPermission(interceptor, user)) {
                throw new ServerException(ServerStatus.ON_DENIED);
            }

            return AopProxyHandler.VOID;
        }

    }
}
