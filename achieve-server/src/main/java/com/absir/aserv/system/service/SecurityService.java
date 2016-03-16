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
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.helper.HelperLong;
import com.absir.aserv.system.security.ISecurityService;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.security.SecurityManager;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.core.kernel.KernelString;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.IGet;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.servlet.InputRequest;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configure
public abstract class SecurityService implements ISecurityService, ISecurity, IGet {

    public static final SecurityService ME = BeanFactoryUtils.get(SecurityService.class);

    protected static final String SECURITY_SESSION_NAME = SecurityService.class.getName() + "@"
            + "SECURITY_SESSION_NAME" + "@";

    private static final String SECURITY_USER_NAME = "USER";

    private static final String SECURITY_CONTEXT_NAME = "SECURITY";

    private static final int SECURITY_CREATE_MAX_COUNT = 3;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private Map<String, SecurityManager> securityManagerMap;

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

    protected abstract void loginSecurity(SecurityContext securityContext, JiUserBase userBase);

    protected abstract JbSession createSecuritySession();

    protected abstract SecurityContext createSecurityContext(JiUserBase userBase, JbSession session, String sessionId);

    public String getAddress(HttpServletRequest request) {
        return request.getLocalAddr();
    }

    public SecurityContext loginUser(SecurityManager securityManager, JiUserBase userBase, long remember,
                                     String address, String agent, Input input) {
        long contextTime = ContextUtils.getContextTime();
        JbSession session = createSecuritySession();
        session.setUserId(userBase.getUserId());
        session.setUsername(userBase.getUsername());
        session.setAddress(address);
        session.setAgent(agent);
        session.setIp(address == null ? 0 : HelperLong.longIPV4(address));
        session.setLastTime(contextTime + securityManager.getSessionExpiration());

        SecurityContext securityContext;
        int count = 0;
        while (true) {
            securityContext = createSecurityContext(userBase, session, VerifierService.ME.randVerifierId(input));
            if (securityContext != null) {
                break;
            }

            if (++count >= SECURITY_CREATE_MAX_COUNT) {
                return null;
            }
        }

        securityContext.setUser(userBase);
        securityContext.setAddress(address);
        securityContext.setAgent(agent);
        securityContext.setLifeTime(securityManager.getSessionLife());
        securityContext.retainAt(contextTime);
        long sessionExpiration = securityManager.getSessionExpiration();
        if (sessionExpiration >= 0 && sessionExpiration < remember) {
            sessionExpiration = remember;
        }

        securityContext.setMaxExpirationTime(sessionExpiration);
        loginSecurity(securityContext, userBase);
        setSecurityContext(securityContext, input);
        return securityContext;
    }

    public SecurityContext loginUserRequest(String name, JiUserBase userBase, InputRequest inputRequest) {
        SecurityManager securityManager = SecurityService.ME.getSecurityManager(name);
        long remember = securityManager.getSessionExpiration();
        if (remember < securityManager.getSessionLife()) {
            remember = securityManager.getSessionLife();
        }

        return SecurityService.ME.loginUserRequest(securityManager, userBase, remember, inputRequest);
    }

    public SecurityContext loginUserRequest(SecurityManager securityManager, JiUserBase userBase, long remember,
                                            InputRequest inputRequest) {
        SecurityContext securityContext = loginUser(securityManager, userBase, remember, inputRequest.getAddress(), inputRequest.getRequest().getHeader("user-agent"), inputRequest);
        if (securityContext != null) {
            if (remember > 0) {
                inputRequest.setCookie(securityManager.getSessionKey(), securityContext.getId(), securityManager.getCookiePath(),
                        remember);
            }
        }

        return securityContext;
    }

    protected abstract SecurityContext findSecurityContext(String sessionId, SecurityManager securityManager);

    @Override
    public SecurityContext autoLogin(String name, boolean remember, int roleLevel, Input input) {
        SecurityContext securityContext = getSecurityContext(input);
        if (securityContext == null) {
            if (input instanceof InputRequest) {
                InputRequest inputRequest = (InputRequest) input;
                SecurityManager securityManager = getSecurityManager(name);
                String sessionId = inputRequest.getSession(securityManager.getSessionKey());
                if (sessionId == null && remember) {
                    sessionId = inputRequest.getCookie(securityManager.getSessionKey());
                }

                if (sessionId != null) {
                    securityContext = ContextUtils.findContext(SecurityContext.class, sessionId);
                    if (securityContext == null) {
                        securityContext = ME.findSecurityContext(sessionId, securityManager);
                        if (securityContext == null) {
                            return null;
                        }
                    }

                    securityContext.retainAt();
                    setSecurityContext(securityContext, input);
                }
            }
        }

        if (securityContext == null) {
            return null;

        } else {
            JiUserBase user = securityContext.getUser();
            if (user == null || user.isDisabled() || user.getUserRoleLevel() < roleLevel) {
                return null;
            }
        }

        return securityContext;
    }

    @Override
    public SecurityContext login(String username, String password, long remember, int roleLevel, String name,
                                 Input input) {
        if (input instanceof InputRequest) {
            InputRequest inputRequest = (InputRequest) input;
            JiUserBase userBase = ME.getUserBase(username);
            if (userBase == null || userBase.isDisabled()) {
                throw new ServerException(ServerStatus.NO_USER);
            }

            SecurityManager securityManager = getSecurityManager(name);
            if (!validator(userBase, password, securityManager.getError(), securityManager.getErrorTime())) {
                JLog.log(name, "login", input.getAddress(), username, false);
                throw new ServerException(ServerStatus.NO_USER, userBase);
            }

            if (userBase.getUserRoleLevel() >= roleLevel) {
                SecurityContext securityContext = loginUserRequest(securityManager, userBase, remember, inputRequest);
                if (securityContext != null) {
                    JLog.log(name, "login", input.getAddress(), username, true);
                    inputRequest.setSession(securityManager.getSessionKey(), securityContext.getId());
                    return securityContext;
                }
            }
        }

        return null;
    }

    @Override
    public void logout(String name, Input input) {
        SecurityContext securityContext = autoLogin(name, false, 0, input);
        if (securityContext == null) {
            setUserBase(null, input);

        } else {
            // 销毁之前的登录
            securityContext.destorySession();
            if (input instanceof InputRequest) {
                InputRequest inputRequest = (InputRequest) input;
                SecurityManager securityManager = getSecurityManager(name);
                inputRequest.removeSession(securityManager.getSessionKey());
                inputRequest.removeCookie(securityManager.getSessionKey(), securityManager.getCookiePath());
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
        JiUserBase user = getUserBase(input);
        if (user != null) {
            value = DynaBinderUtils.to(user.getMetaMap(name), toClass);
        }

        if (value == null && input instanceof InputRequest) {
            value = DynaBinderUtils.to(((InputRequest) input).getSession(SECURITY_SESSION_NAME + name), toClass);
        }

        return value;
    }

    public void setSession(final String name, final Object value, Input input) {
        JiUserBase user = getUserBase(input);
        if (user == null) {
            if (input instanceof InputRequest) {
                ((InputRequest) input).setSession(SECURITY_SESSION_NAME + name,
                        DynaBinderUtils.to(value, String.class));
            }

        } else {
            BeanService.MERGE.merge(user, user.getUserId(), new CallbackTemplate<Object>() {

                @Override
                public void doWith(Object template) {
                    ((JiUserBase) template).setMetaMap(name, DynaBinderUtils.to(value, String.class));
                }
            });
        }
    }

    @Override
    public String getAddress(Input input) {
        return null;
    }

    @Override
    public Integer getLocaleCode(Input input) {
        Integer locale = getSession("locale", Integer.class, input);
        if (locale == null && input instanceof InputRequest) {
            locale = LangBundle.ME.getLocaleCode(((InputRequest) input).getRequest().getLocale());
            if (locale != null) {
                setSession("locale", locale, input);
            }
        }

        return locale;
    }
}
