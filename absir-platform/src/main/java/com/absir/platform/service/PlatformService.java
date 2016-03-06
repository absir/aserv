/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 上午10:46:28
 */
package com.absir.platform.service;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.bean.JPlatformUserRef;
import org.hibernate.Session;

import java.util.Iterator;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
@Base
@Bean
public class PlatformService {

	/** ME */
	public static final PlatformService ME = BeanFactoryUtils.get(PlatformService.class);

	/**
	 * @param platform
	 * @param username
	 * @return
	 */
	public static String getPlatformUserRefId(String platform, String username) {
		return platform + ',' + username;
	}

	/**
	 * 查询平台用户
	 * 
	 * @param platform
	 * @param username
	 * @return
	 */
	@Transaction(readOnly = true)
	public JPlatformUser findPlatformUser(String platform, String username) {
		JPlatformUserRef ref = BeanDao.get(BeanDao.getSession(), JPlatformUserRef.class,
				getPlatformUserRefId(platform, username));
		return ref == null ? null : ref.getPlatformUser();
	}

	/**
	 * @param platform
	 * @param username
	 * @param channel
	 * @return
	 */
	@Transaction
	public JPlatformUser createPlatformUser(String platform, String username, String channel) {
		JPlatformUser platformUser = new JPlatformUser();
		platformUser.setPlatform(platform);
		platformUser.setUsername(username);
		platformUser.setChannel(channel);
		Session session = BeanDao.getSession();
		try {
			session.persist(platformUser);
			session.flush();

		} catch (Exception e) {
			session.clear();
			Iterator<JPlatformUser> iterator = QueryDaoUtils.createQueryArray(session,
					"SELECT o FROM JPlatformUser o WHERE o.platform = ? AND o.username = ?", platform, username)
					.iterate();
			platformUser = iterator.hasNext() ? iterator.next() : null;
		}

		if (platformUser != null) {
			createPlatformUser(platformUser, session);
		}

		return platformUser;
	}

	/**
	 * @param platformUser
	 * @param session
	 */
	protected void createPlatformUser(JPlatformUser platformUser, Session session) {
		JPlatformUserRef ref = new JPlatformUserRef();
		ref.setId(getPlatformUserRefId(platformUser.getPlatform(), platformUser.getUsername()));
		ref.setPlatformUser(platformUser);
		session.persist(ref);
	}

	/**
	 * @param platform
	 * @param username
	 * @param channel
	 * @return
	 */
	@Transaction
	public JPlatformUser getPlatformUser(String platform, String username, String channel) {
		JPlatformUser platformUser = findPlatformUser(platform, username);
		if (platformUser == null) {
			try {
				platformUser = createPlatformUser(platform, username, channel);

			} catch (Exception e) {
				BeanDao.getSession().clear();
				platformUser = findPlatformUser(platform, username);
			}
		}

		return platformUser;
	}

	/**
	 * 获取关联平台账号
	 * 
	 * @param userBase
	 * @param channel
	 * @return
	 */
	public static JPlatformUser getPlatformUser(JiUserBase userBase, String channel) {
		if (userBase instanceof JPlatformUser) {
			return (JPlatformUser) userBase;
		}

		return ME.getPlatformUser("JUser", String.valueOf(userBase.getUserId()), channel);
	}

	/**
	 * 验证登录
	 * 
	 * @param platform
	 * @param username
	 * @param sessionId
	 * @return
	 */
	public JPlatformUser platformUserSession(String platform, String username, String sessionId) {
		JPlatformUser platformUser = findPlatformUser(platform, username);
		return platformUser == null || !KernelObject.equals(sessionId, platformUser.getSessionId()) ? null
				: platformUser;
	}

	/**
	 * 登录平台账号
	 * 
	 * @param platform
	 * @param username
	 * @param channel
	 * @param lifeTime
	 * @return
	 */
	@Transaction
	public JPlatformUser loginPlatformUser(String platform, String username, String channel, long lifeTime) {
		return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 0);
	}

	/**
	 * 重新登录
	 * 
	 * @param platform
	 * @param username
	 * @param channel
	 * @param lifeTime
	 * @return
	 */
	@Transaction
	public JPlatformUser reLoginPlatformUser(String platform, String username, String channel, long lifeTime) {
		return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 1);
	}

	/**
	 * 刷新登录信息
	 * 
	 * @param platform
	 * @param username
	 * @param channel
	 * @param lifeTime
	 * @return
	 */
	@Transaction
	public JPlatformUser reSessionPlatformUser(String platform, String username, String channel, long lifeTime) {
		return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 2);
	}

	/**
	 * @param platformUser
	 * @param lifeTime
	 * @param type
	 *            0 login 1 continue login 2 session
	 * @return
	 */
	@Transaction
	public JPlatformUser loginSessionUserType(JPlatformUser platformUser, long lifeTime, int type) {
		if (platformUser != null && lifeTime > 0) {
			long currentTime = System.currentTimeMillis();
			if (type != 0 || platformUser.getPassTime() < currentTime) {
				platformUser.setPassTime(currentTime + lifeTime);
				if (type == 2 || KernelString.isEmpty(platformUser.getSessionId())) {
					platformUser
							.setSessionId(HelperRandom.randSecondId(currentTime, 8, platformUser.getId().hashCode()));
				}
			}

			BeanDao.getSession().merge(platformUser);
		}

		return platformUser;
	}
}
