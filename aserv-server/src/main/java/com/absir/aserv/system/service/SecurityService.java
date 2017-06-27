/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-31 下午5:16:29
 */
package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.support.developer.IDeveloper.ISecurity;
import com.absir.aserv.system.bean.JLog;
import com.absir.aserv.system.bean.JbSession;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.IUser;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.helper.HelperLong;
import com.absir.aserv.system.security.ISecurityService;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.security.SecurityManager;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Domain;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.context.core.ContextDaemon;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.IAfterInvoker;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;

import javax.servlet.ServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Configure
public abstract class SecurityService implements ISecurityService, ISecurity, IAfterInvoker<SecurityContext> {

    public static final String USER_NO_LOGIN = LangCodeUtils.get("用户没有登录", SecurityService.class);

    public static final String PASSWORD_ERROR = LangCodeUtils.get("密码错误", SecurityService.class);

    public static final SecurityService ME = BeanFactoryUtils.get(SecurityService.class);

    protected static final String SECURITY_SESSION_NAME = SecurityService.class.getName() + "@"
            + "SECURITY_SESSION_NAME" + "@";

    private static final String SECURITY_USER_NAME = "USER";

    private static final String SECURITY_CONTEXT_NAME = "SECURITY";

    @Value("security.max.persist")
    private static final int SECURITY_MAX_PERSIST = 3;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private Map<String, SecurityManager> securityManagerMap;

    @Value("security.context.session")
    private int securityContextSession;

    @Domain
    private DSequence sessionSequence;

    public JiUserBase getUserBase(Input input) {
        Object user = input.getModel().get(SECURITY_USER_NAME);
        return user == null || !(user instanceof JiUserBase) ? null : (JiUserBase) user;
    }

    public void setUserBase(JiUserBase userBase, Input input) {
        input.getModel().put(SECURITY_USER_NAME, userBase);
    }

    public SecurityContext getSecurityContext(Input input) {
        Object securityContext = input.getModel().get(SECURITY_CONTEXT_NAME);
        return securityContext == null || !(securityContext instanceof SecurityContext) ? null
                : (SecurityContext) securityContext;
    }

    public void setSecurityContext(SecurityContext securityContext, Input input) {
        input.getModel().put(SECURITY_CONTEXT_NAME, securityContext);
        setUserBase(securityContext == null ? null : securityContext.getUser(), input);
    }

    public SecurityManager getSecurityManager(String name) {
        SecurityManager securityManager = KernelString.isEmpty(name) ? null : securityManagerMap.get(name);
        return securityManager == null ? this.securityManager : securityManager;
    }

    protected abstract JbSession createSession(JiUserBase userBase, long remember,
                                               String address, String agent);

    public String nextSecurityId() {
        return sessionSequence.getNextDigLetterId();
    }

    public void insertSession(JiUserBase userBase, JbSession session) {
        BeanService.ME.persist(session);
    }

    public void deleteSession(JbSession session) {
        BeanService.ME.delete(session);
    }

    public void updateSession(JiUserBase userBase, JbSession session) {
        BeanService.ME.merge(session);
    }

    public JbSession findSession(String sessionId) {
        return (JbSession) BeanService.ME.selectQuerySingle("SELECT o FROM JSession o WHERE o.id = ? AND o.disable = ? AND o.passTime > ?", sessionId, false, ContextUtils.getContextTime());
    }

    public JiUserBase findUserBase(JbSession session) {
        return ME.getUserBase(session.getUserId(), session.getRoleLevel());
    }

    protected SecurityContext createSecurityContext(String sessionId) {
        return new SecurityContext();
    }

    protected Class<? extends SecurityContext> getFactorySecurityContextClass() {
        return SecurityContext.class;
    }

    protected SecurityContext createSecurityContext(SecurityManager securityManager, JiUserBase userBase, JbSession session, long remember, Input input) {
        SecurityContext securityContext = null;
        if (securityContextSession == 1) {
            securityContext = createSecurityContext(session.getId());

        } else {
            long contextTime = ContextUtils.getContextTime();
            securityContext = ContextUtils.getContext(getFactorySecurityContextClass(), session.getId());
            securityContext.setLifeTime(securityManager.getSessionLife());
            securityContext.retainAt(contextTime);
            securityContext.setMaxExpirationTime(ContextUtils.getContextTime() + remember);
            securityContext.retainAt();
        }

        if ((securityContextSession == 1 || securityContextSession == 2) && input != null) {
            input.addAfterInvoker(securityContext, this);
        }

        securityContext.setSession(session);
        securityContext.setUser(userBase);
        return securityContext;
    }

    public long getRemember(SecurityManager securityManager, long remember) {
        long sessionExpiration = securityManager.getSessionExpiration();
        if (remember <= 0 || (sessionExpiration > 0 && remember > sessionExpiration)) {
            remember = sessionExpiration;
        }

        return sessionExpiration > 0 && sessionExpiration > remember ? sessionExpiration : remember;
    }

    public JbSession loginUser(JiUserBase userBase, long remember,
                               String address, String agent) {
        JbSession session = createSession(userBase, remember, address, agent);
        if (session == null) {
            return null;
        }

        Long userId = session.getUserId();
        if (userId == null || userId == 0) {
            session.setUserId(userBase.getUserId());
        }

        session.setUsername(userBase.getUsername());
        session.setRoleLevel(userBase.getUserRoleLevel());
        if (address != null) {
            session.setAddress(address);
            session.setIp(HelperLong.longIPV4(address));
        }

        session.setAgent(agent);
        long contextTime = ContextUtils.getContextTime();
        session.setLastTime(contextTime);
        session.setPassTime(contextTime + remember);
        int count = 0;
        while (true) {
            session.setId(nextSecurityId());
            insertSession(userBase, session);
            if (session != null) {
                break;
            }

            if (++count >= SECURITY_MAX_PERSIST) {
                return null;
            }
        }

        return session;
    }

    protected void loginSecurity(SecurityContext securityContext, JiUserBase userBase, Input input) {
    }


    public SecurityContext loginUser(String name, JiUserBase userBase, Input input) {
        SecurityManager securityManager = SecurityService.ME.getSecurityManager(name);
        long remember = securityManager.getSessionExpiration();
        if (remember < securityManager.getSessionLife()) {
            remember = securityManager.getSessionLife();
        }

        return SecurityService.ME.loginUser(securityManager, userBase, remember, input);
    }

    public SecurityContext loginUser(SecurityManager securityManager, JiUserBase userBase, long remember,
                                     Input input) {
        return loginUser(securityManager, userBase, remember, input.getAddress(), input.getFacade().getUserAgent(), input);
    }

    public SecurityContext loginUser(SecurityManager securityManager, JiUserBase userBase, long remember,
                                     String address, String agent, Input input) {
        remember = getRemember(securityManager, remember);
        JbSession session = loginUser(userBase, remember, address, agent);
        if (session == null) {
            return null;
        }

        SecurityContext securityContext = createSecurityContext(securityManager, userBase, session, remember, input);
        if (securityContext == null) {
            return null;
        }

        setSecurityContext(securityContext, input);
        loginSecurity(securityContext, userBase, input);
        if (input.isInFacade()) {
            if (remember > 0) {
                input.getFacade().setCookie(securityManager.getSessionKey(), securityContext.getId(), securityManager.getCookiePath(), remember);

            } else {
                input.getFacade().setSession(securityManager.getSessionKey(), securityContext.getId());
            }

            return securityContext;
        }

        return securityContext;
    }

    protected SecurityContext findSecurityContext(String sessionId, SecurityManager securityManager, Input input) {
        if (securityContextSession != 1) {
            SecurityContext securityContext = ContextUtils.findContext(getFactorySecurityContextClass(), sessionId);
            if (securityContext != null) {
                return securityContext;
            }
        }

        JbSession session = findSession(sessionId);
        if (session == null || session.isDisable()) {
            return null;
        }

        long remember = session.getPassTime() - ContextUtils.getContextTime();
        if (remember <= 0) {
            return null;
        }

        JiUserBase userBase = findUserBase(session);
        return createSecurityContext(securityManager, userBase, session, remember, input);
    }

    @Override
    public SecurityContext autoLogin(String name, boolean remember, int roleLevel, Input input) {
        SecurityContext securityContext = getSecurityContext(input);
        if (securityContext == null) {
            String daemon = input.getParam("__daemon__");
            if (!KernelString.isEmpty(daemon)) {
                if (ContextDaemon.ME.isDeveloper(daemon)) {
                    // __daemon__ developer user
                    securityContext = new SecurityContext();
                    securityContext.setUser(new JiUserBase() {
                        @Override
                        public Long getUserId() {
                            return -99L;
                        }

                        @Override
                        public boolean isDeveloper() {
                            return true;
                        }

                        @Override
                        public boolean isActivation() {
                            return true;
                        }

                        @Override
                        public boolean isDisabled() {
                            return false;
                        }

                        @Override
                        public String getUsername() {
                            return "__daemon__";
                        }

                        @Override
                        public int getUserRoleLevel() {
                            return 99;
                        }

                        @Override
                        public Collection<? extends JiUserRole> userRoles() {
                            return null;
                        }

                        @Override
                        public Collection<? extends JbUserRole> getUserRoles() {
                            return null;
                        }

                        @Override
                        public Object getMetaMap(String key) {
                            return null;
                        }

                        @Override
                        public void setMetaMap(String key, String value) {

                        }
                    });
                    setSecurityContext(securityContext, input);
                    return securityContext;
                }
            }

            if (input.isInFacade()) {
                SecurityManager securityManager = getSecurityManager(name);
                String sessionId = null;
                if (remember) {
                    sessionId = input.getFacade().getCookie(securityManager.getSessionKey());
                }

                if (sessionId == null) {
                    sessionId = input.getFacade().getSessionValue(securityManager.getSessionKey());
                }

                if (sessionId != null) {
                    securityContext = findSecurityContext(sessionId, securityManager, input);
                    if (securityContext == null) {
                        return null;
                    }

                } else {
                    return null;
                }

            } else {
                return null;
            }
        }

        JiUserBase user = securityContext.getUser();
        if (user == null || user.isDisabled() || user.getUserRoleLevel() < roleLevel) {
            return null;
        }

        securityContext.retainAt();
        setSecurityContext(securityContext, input);
        return securityContext;
    }

    @Override
    public SecurityContext login(String username, String password, long remember, int roleLevel, String name,
                                 Input input) {
        JiUserBase userBase = ME.getUserBase(username, roleLevel);
        if (userBase == null || userBase.isDisabled()) {
            throw new ServerException(ServerStatus.NO_USER);
        }

        SecurityManager securityManager = getSecurityManager(name);
        if (!validator(userBase, password, securityManager.getError(), securityManager.getErrorTime(), input.getAddress())) {
            JLog.log(name, "login", input.getAddress(), username, false);
            throw new ServerException(ServerStatus.NO_USER, userBase);
        }

        if (userBase.getUserRoleLevel() >= roleLevel) {
            SecurityContext securityContext = loginUser(securityManager, userBase, remember, input);
            if (securityContext != null) {
                JLog.log(name, "login", input.getAddress(), username, true);
                return securityContext;
            }
        }

        return null;
    }

    @Override
    public void logout(String name, Input input) {
        SecurityContext securityContext = autoLogin(name, true, 0, input);
        if (securityContext == null) {
            setUserBase(null, input);

        } else {
            // 销毁之前的登录
            securityContext.destroySession();
            if (input.isInFacade()) {
                SecurityManager securityManager = getSecurityManager(name);
                input.getFacade().removeSession(securityManager.getSessionKey());
                input.getFacade().removeCookie(securityManager.getSessionKey(), securityManager.getCookiePath());
            }

            setSecurityContext(null, input);
        }
    }

    @Override
    public JiUserBase loginRender(Object render) {
        Input input = null;
        if (render instanceof Input) {
            input = (Input) render;

        } else if (render instanceof ServletRequest) {
            input = Pag.getInput((ServletRequest) render);
        }

        if (input == null) {
            input = OnPut.input();
        }

        SecurityContext securityContext = input == null ? null
                : autoLogin("admin", true, JeRoleLevel.ROLE_ADMIN.ordinal(), input);
        return securityContext == null ? null : securityContext.getUser();
    }

    public <T> T getSession(String name, Class<T> toClass, Input input) {
        T value = null;
        SecurityContext securityContext = getSecurityContext(input);
        if (securityContext != null) {
            value = DynaBinderUtils.to(securityContext.getSession().getMetaMap(name), toClass);
        }

        if (value == null) {
            value = DynaBinderUtils.to(input.getFacade().getSession(SECURITY_SESSION_NAME + name), toClass);
        }

        return value;
    }

    public void setSession(final String name, final Object value, Input input) {
        SecurityContext securityContext = getSecurityContext(input);
        if (securityContext == null) {
            input.getFacade().setSession(SECURITY_SESSION_NAME + name, DynaBinderUtils.to(value, String.class));

        } else {
            securityContext.getSession().setMetaMap(name, DynaBinderUtils.to(value, String.class));
            securityContext.setChanged(true);
        }
    }

    @Override
    public boolean validator(JiUserBase userBase, String password, int error, long errorTime, String address) {
        if (password == null) {
            return true;
        }

        if (!(userBase instanceof IUser)) {
            return false;
        }

        IUser user = (IUser) userBase;
        int errorLogin = user.getErrorLogin();
        long contextTime = ContextUtils.getContextTime();
        if (user.getLastErrorLogin() <= contextTime) {
            errorLogin = 0;
        }

        if (error > 0 && errorLogin >= error) {
            return false;
        }

        if (PasswordCrudFactory.getPasswordEncrypt(password, user.getSalt(), user.getSaltCount()).equals(user.getPassword())) {
            user.setErrorLogin(0);
            user.setLastLogin(contextTime);
            user.setLoginTimes(user.getLoginTimes() + 1);
            user.setLoginAddress(address);
            BeanService.ME.merge(user);
            return true;
        }

        // 密码错误error次,errorTime时间内禁止登录
        if (error != 0) {
            user.setErrorLogin(++errorLogin);
            user.setLastErrorLogin(contextTime + errorTime);
            user.setLastErrorTimes(error - errorLogin);
            BeanService.ME.merge(user);

        } else {
            user.setLastErrorTimes(-1);
        }

        return false;
    }

    @Transaction(readOnly = true)
    public void mergeUserBase(String entityName, JiUserBase entity, IEntityMerge.MergeType mergeType, Object mergeEvent) {
        if (getFactorySecurityContextClass() != null) {
            Iterator<String> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                    "SELECT o.id FROM JSession o WHERE o.userId = ? AND o.passTime > ?", entity.getUserId(),
                    ContextUtils.getContextTime()).iterate();
            while (iterator.hasNext()) {
                SecurityContext securityContext = ContextUtils.findContext(SecurityContext.class, iterator.next());
                if (securityContext != null) {
                    if (mergeType == IEntityMerge.MergeType.UPDATE) {
                        securityContext.setUser(entity);

                    } else {
                        securityContext.setExpiration();
                    }
                }
            }
        }
    }

    @Override
    public void afterInvoker(SecurityContext obj) {
        obj.unInitialize();
    }

}
