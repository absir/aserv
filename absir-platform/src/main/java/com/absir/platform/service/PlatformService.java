/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 上午10:46:28
 */
package com.absir.platform.service;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Domain;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.JPlatformSession;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.bean.JPlatformUserRef;
import org.hibernate.Session;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class PlatformService {

    public static final PlatformService ME = BeanFactoryUtils.get(PlatformService.class);

    @Value("platform.lifeTime")
    private long lifeTime = 3600000 * 24;

    @Domain
    private DSequence sessionSequence;

    public static String getPlatformUserRefId(String platform, String username) {
        return platform + '@' + username;
    }

    /**
     * 获取关联平台账号
     */
    public static JPlatformUser getPlatformUser(JiUserBase userBase, String channel) {
        if (userBase instanceof JPlatformUser) {
            return (JPlatformUser) userBase;
        }

        return ME.getPlatformUser("JUser", String.valueOf(userBase.getUserId()), channel);
    }

    public long getLifeTime() {
        return lifeTime;
    }

    /**
     * 查询平台用户
     */
    @Transaction(readOnly = true)
    public JPlatformUser findPlatformUser(String platform, String username) {
        JPlatformUserRef ref = BeanDao.get(BeanDao.getSession(), JPlatformUserRef.class,
                getPlatformUserRefId(platform, username));
        return ref == null ? null : ref.getPlatformUser();
    }

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

    protected void createPlatformUser(JPlatformUser platformUser, Session session) {
        JPlatformUserRef ref = new JPlatformUserRef();
        ref.setId(getPlatformUserRefId(platformUser.getPlatform(), platformUser.getUsername()));
        ref.setPlatformUser(platformUser);
        session.persist(ref);
    }

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
     * 验证登录
     */
    public JPlatformUser platformUserSession(String platform, String username, String sessionId) {
        JPlatformUser platformUser = findPlatformUser(platform, username);
        return platformUser == null || !KernelObject.equals(sessionId, platformUser.getSessionId()) ? null
                : platformUser;
    }

    public JPlatformUser loginPlatformUser(String platform, String username, String channel) {
        return loginPlatformUser(platform, username, channel, lifeTime);
    }

    /**
     * 登录平台账号
     */
    @Transaction
    public JPlatformUser loginPlatformUser(String platform, String username, String channel, long lifeTime) {
        return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 0);
    }

    /**
     * 重新登录
     */
    @Transaction
    public JPlatformUser reLoginPlatformUser(String platform, String username, String channel, long lifeTime) {
        return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 1);
    }

    /**
     * 刷新登录信息
     */
    @Transaction
    public JPlatformUser reSessionPlatformUser(String platform, String username, String channel, long lifeTime) {
        return loginSessionUserType(getPlatformUser(platform, username, channel), lifeTime, 2);
    }

    public String nextSessionId() {
        return sessionSequence.getNextHexId();
    }

    /**
     * @param type 0 不自动延长登录时间 | 1 | 2 强制刷sessionID
     */
    @Transaction
    public JPlatformUser loginSessionUserType(JPlatformUser platformUser, long lifeTime, int type) {
        if (platformUser != null && lifeTime > 0) {
            long contextTime = ContextUtils.getContextTime();
            if (type != 0 || platformUser.getPassTime() < contextTime) {
                platformUser.setPassTime(contextTime + lifeTime);
                if (type == 2 || KernelString.isEmpty(platformUser.getSessionId())) {
                    platformUser.setSessionId(nextSessionId());
                }

                BeanDao.getSession().merge(platformUser);
            }
        }

        return platformUser;
    }

    public JPlatformSession loginReSession(String platform, String username, String channel, String address, String agent) {
        return loginReSession(getPlatformUser(platform, username, channel), address, agent);
    }

    public JPlatformSession loginReSession(JPlatformUser platformUser, String address, String agent) {
        return loginReSession(platformUser, lifeTime, address, agent);
    }

    public JPlatformSession loginReSession(JPlatformUser platformUser, long lifeTime, String address, String agent) {
        JPlatformSession platformSession = new JPlatformSession();
        platformSession.setId(nextSessionId());
        platformSession.setPlatformUserId(platformUser.getId());
        platformSession.setPassTime(ContextUtils.getContextTime() + lifeTime);
        platformSession.setAddress(address);
        platformSession.setAgent(agent);
        BeanService.ME.persist(platformSession);
        return platformSession;
    }

    public JPlatformUser loginFromPlatformUser(String platform, String username, String sessionId) {
        JPlatformUser platformUser = findPlatformUser(platform, username);
        if (platformUser != null) {
            if (platformUser.getPassTime() > ContextUtils.getContextTime() && !KernelString.isEmpty(platformUser.getSessionId()) && platformUser.getSessionId().equals(sessionId)) {
                return platformUser;
            }
        }

        return null;
    }

    public JPlatformUser loginForSessionId(String sessionId) {
        JPlatformSession platformSession = BeanService.ME.get(JPlatformSession.class, sessionId);
        if (platformSession != null && platformSession.getPassTime() > ContextUtils.getContextTime()) {
            return BeanService.ME.get(JPlatformUser.class, platformSession.getPlatformUserId());
        }

        return null;
    }
}
