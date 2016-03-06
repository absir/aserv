/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-31 下午5:16:29
 */
package com.absir.aserv.system.service;

import java.util.Map;

import javax.servlet.ServletRequest;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.support.developer.IDeveloper.ISecurity;
import com.absir.aserv.system.bean.JLog;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeRoleLevel;
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
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
@Configure
public abstract class SecurityService implements ISecurityService, ISecurity, IGet {

	/** ME */
	public static final SecurityService ME = BeanFactoryUtils.get(SecurityService.class);

	/** securityManager */
	@Inject
	private SecurityManager securityManager;

	/** securityManagerMap */
	@Inject
	private Map<String, SecurityManager> securityManagerMap;

	/** SECURITY_USER_NAME */
	private static final String SECURITY_USER_NAME = "USER";

	/** SECURITY_CONTEXT_NAME */
	private static final String SECURITY_CONTEXT_NAME = "SECURITY";

	/**
	 * @param input
	 * @return
	 */
	public JiUserBase getUserBase(Input input) {
		Object user = input.getModel().get(SECURITY_USER_NAME);
		return user == null || !(user instanceof JiUserBase) ? null : (JiUserBase) user;
	}

	/**
	 * @param userBase
	 * @param input
	 */
	public void setUserBase(JiUserBase userBase, Input input) {
		input.getModel().put(SECURITY_USER_NAME, userBase);
	}

	/**
	 * @param input
	 * @return
	 */
	public SecurityContext getSecurityContext(Input input) {
		Object securityContext = input.getModel().get(SECURITY_CONTEXT_NAME);
		return securityContext == null || !(securityContext instanceof SecurityContext) ? null
				: (SecurityContext) securityContext;
	}

	/**
	 * @param securityContext
	 * @param input
	 */
	public void setSecurityContext(SecurityContext securityContext, Input input) {
		input.getModel().put(SECURITY_CONTEXT_NAME, securityContext);
		setUserBase(securityContext == null ? null : securityContext.getUser(), input);
	}

	/**
	 * @param name
	 * @return
	 */
	public SecurityManager getSecurityManager(String name) {
		SecurityManager securityManager = KernelString.isEmpty(name) ? null : securityManagerMap.get(name);
		return securityManager == null ? this.securityManager : securityManager;
	}

	/**
	 * @param name
	 * @param userBase
	 * @param inputRequest
	 * @return
	 */
	public SecurityContext loginUser(String name, JiUserBase userBase, InputRequest inputRequest) {
		SecurityManager securityManager = SecurityService.ME.getSecurityManager(name);
		long remember = securityManager.getSessionExpiration();
		if (remember < securityManager.getSessionLife()) {
			remember = securityManager.getSessionLife();
		}

		return SecurityService.ME.loginUser(securityManager, userBase, remember, inputRequest);
	}

	/**
	 * @param securityContext
	 * @param userBase
	 */
	protected abstract void loginSecurity(SecurityContext securityContext, JiUserBase userBase);

	/**
	 * @param userBase
	 * @param sessionId
	 * @return
	 */
	protected abstract SecurityContext createSecurityContext(JiUserBase userBase, String sessionId);

	/**
	 * @param securityManager
	 * @param userBase
	 * @param remember
	 * @param inputRequest
	 * @return
	 */
	public SecurityContext loginUser(SecurityManager securityManager, JiUserBase userBase, long remember,
			InputRequest inputRequest) {
		long contextTime = ContextUtils.getContextTime();
		String sessionId = VerifierService.ME.randVerifierId(inputRequest.getRequest());
		SecurityContext securityContext = createSecurityContext(userBase, sessionId);
		securityContext.setUser(userBase);
		securityContext.setAddress(inputRequest.getAddress());
		securityContext.setAgent(inputRequest.getRequest().getHeader("user-agent"));
		securityContext.setLifeTime(securityManager.getSessionLife());
		securityContext.retainAt(contextTime);
		long sessionExpiration = securityManager.getSessionExpiration();
		if (sessionExpiration >= 0 && sessionExpiration < remember) {
			sessionExpiration = remember;
		}

		securityContext.setMaxExpirationTime(sessionExpiration);
		loginSecurity(securityContext, userBase);
		setSecurityContext(securityContext, inputRequest);
		if (remember > 0) {
			inputRequest.setCookie(securityManager.getSessionKey(), sessionId, securityManager.getCookiePath(),
					remember);
		}

		return securityContext;
	}

	/**
	 * @param sessionId
	 * @param securityManager
	 * @return
	 */
	protected abstract SecurityContext findSecurityContext(String sessionId, SecurityManager securityManager);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.security.ISecurityService#autoLogin(java.lang
	 * .String, boolean, int, com.absir.server.in.Input)
	 */
	@Override
	public SecurityContext autoLogin(String name, boolean remeber, int roleLevel, Input input) {
		SecurityContext securityContext = getSecurityContext(input);
		if (securityContext == null) {
			if (input instanceof InputRequest) {
				InputRequest inputRequest = (InputRequest) input;
				SecurityManager securityManager = getSecurityManager(name);
				String sessionId = inputRequest.getSession(securityManager.getSessionKey());
				if (sessionId == null && remeber) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.security.ISecurityService#login(java.lang.String
	 * , java.lang.String, long, int, java.lang.String,
	 * com.absir.server.in.Input)
	 */
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
				SecurityContext securityContext = loginUser(securityManager, userBase, remember, inputRequest);
				if (securityContext != null) {
					JLog.log(name, "login", input.getAddress(), username, true);
					inputRequest.setSession(securityManager.getSessionKey(), securityContext.getId());
					return securityContext;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.security.ISecurityService#logout(java.lang.
	 * String , com.absir.server.in.Input)
	 */
	@Override
	public void logout(String name, Input input) {
		SecurityContext securityContext = autoLogin(name, false, 0, input);
		if (securityContext == null) {
			setUserBase(null, input);

		} else {
			// 销毁之前的登录
			securityContext.setSecuritySupply(null);
			securityContext.setExpiration();
			if (input instanceof InputRequest) {
				InputRequest inputRequest = (InputRequest) input;
				SecurityManager securityManager = getSecurityManager(name);
				inputRequest.removeSession(securityManager.getSessionKey());
				inputRequest.removeCookie(securityManager.getSessionKey(), securityManager.getCookiePath());
			}

			setSecurityContext(null, input);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.support.developer.IDeveloper.ISecurity#loginRender(
	 * java.lang.Object)
	 */
	@Override
	public JiUserBase loginRender(Object render) {
		Input input = null;
		if (render instanceof Input) {
			input = (Input) render;

		} else if (render instanceof ServletRequest) {
			input = Pag.getInput((ServletRequest) render);
		}

		SecurityContext securityContext = input == null ? null
				: autoLogin("admin", true, JeRoleLevel.ROLE_ADMIN.ordinal(), input);
		return securityContext == null ? null : securityContext.getUser();
	}

	/** SECURITY_SESSION_NAME */
	protected static final String SECURITY_SESSION_NAME = SecurityService.class.getName() + "@"
			+ "SECURITY_SESSION_NAME" + "@";

	/**
	 * @param name
	 * @param toClass
	 * @param input
	 * @return
	 */
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

	/**
	 * @param name
	 * @param value
	 * @param input
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.IGet#getLocaleCode(com.absir.server.in.Input)
	 */
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
