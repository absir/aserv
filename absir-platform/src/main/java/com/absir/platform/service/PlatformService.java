/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月18日 上午10:46:28
 */
package com.absir.platform.service;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.bean.JPlatformUserRef;
import org.hibernate.Session;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class PlatformService {

    public static final PlatformService ME = BeanFactoryUtils.get(PlatformService.class);

    public static String getPlatformUserRefId(String platform, String username) {
        return platform + ',' + username;
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

    private DSequence sessionSequence;

    @InjectOrder(255)
    @Inject
    protected void afterPropertySetter() {
        if (sessionSequence == null) {
            sessionSequence = new DSequence();
        }
    }

    public String nextSecurityId() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormatLong(stringBuilder, HelperRandom.FormatType.HEX_DIG, System.currentTimeMillis());
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.HEX_DIG, sessionSequence.nextSequence());
        HelperRandom.randAppendFormat(stringBuilder, 5, HelperRandom.FormatType.HEX_DIG);
        return stringBuilder.toString();
    }


    /**
     * @param type
     * @return
     */
    @Transaction
    public JPlatformUser loginSessionUserType(JPlatformUser platformUser, long lifeTime, int type) {
        if (platformUser != null && lifeTime > 0) {
            long currentTime = System.currentTimeMillis();
            if (type != 0 || platformUser.getPassTime() < currentTime) {
                platformUser.setPassTime(currentTime + lifeTime);
                if (type == 2 || KernelString.isEmpty(platformUser.getSessionId())) {
                    platformUser.setSessionId(nextSecurityId());
                }
            }

            BeanDao.getSession().merge(platformUser);
        }

        return platformUser;
    }
}
