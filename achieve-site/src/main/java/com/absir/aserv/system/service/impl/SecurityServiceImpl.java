/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-31 下午4:59:14
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.system.bean.JSession;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.IUser;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperLong;
import com.absir.aserv.system.security.ISecuritySupply;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.security.SecurityManager;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.UserService;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
@Bean
public class SecurityServiceImpl extends SecurityService implements ISecuritySupply, IEntityMerge<JUser> {

    @Transaction(readOnly = true)
    @Override
    public JiUserBase getUserBase(Long userId) {
        return loadUser(BeanDao.get(BeanDao.getSession(), JUser.class, userId));
    }

    private JUser loadUser(JUser user) {
        if (user != null) {
            user.getUserRoles().isEmpty();
        }

        return user;
    }

    @Transaction(readOnly = true)
    @Override
    public JiUserBase getUserBase(String username) {
        return loadUser(JUserDao.ME.findByUsername(username));
    }

    @Override
    public boolean validator(JiUserBase userBase, String password, int error, long errorTime) {
        if (password == null || !(userBase instanceof IUser)) {
            return true;
        }

        IUser user = (IUser) userBase;
        long contextTime = ContextUtils.getContextTime();
        if (error > 0 && user.getLastErrorLogin() > contextTime) {
            return false;
        }

        if (UserService.getPasswordEntry(password, user).equals(user.getPassword())) {
            return true;
        }

        int errorLogin = user.getErrorLogin() + 1;
        if (errorLogin >= error) {
            // 密码错误5次,30分钟内禁止登录
            errorLogin = 0;
            user.setLastErrorLogin(contextTime + errorTime);
        }

        user.setErrorLogin(errorLogin);
        BeanService.ME.merge(user);
        return false;
    }

    @Override
    protected SecurityContext createSecurityContext(JiUserBase userBase, String sessionId) {
        SecurityContext securityContext = ContextUtils.getContext(SecurityContext.class, sessionId);
        if (userBase.getClass() == JUser.class) {
            securityContext.setSecuritySupply(this);
        }

        return securityContext;
    }

    @Override
    protected void loginSecurity(SecurityContext securityContext, JiUserBase userBase) {
        if (JoEntity.entityClass(userBase.getClass()) == JUser.class) {
            saveSession(securityContext);
        }
    }

    @Override
    @Transaction(readOnly = true)
    protected SecurityContext findSecurityContext(String sessionId, SecurityManager securityManager) {
        Session session = BeanDao.getSession();
        JSession jSession = BeanDao.get(session, JSession.class, sessionId);
        if (jSession != null && jSession.getPassTime() >= ContextUtils.getContextTime()) {
            JUser user = BeanDao.get(session, JUser.class, jSession.getUserId());
            if (user != null) {
                SecurityContext securityContext = ContextUtils.getContext(SecurityContext.class, sessionId);
                securityContext.setUser(loadUser(user));
                if (jSession.getMetas() != null) {
                    Object metas = KernelObject.unserialize(jSession.getMetas());
                    if (metas != null && metas instanceof Map) {
                        securityContext.setMetas((Map<String, Serializable>) metas);
                    }
                }

                securityContext.setLifeTime(securityManager.getSessionLife());
                securityContext.setMaxExpirationTime(jSession.getPassTime());
                return securityContext;
            }
        }

        return null;
    }

    @Override
    public void saveSession(SecurityContext securityContext) {
        JSession session = new JSession();
        session.setId(securityContext.getId());
        JiUserBase userBase = securityContext.getUser();
        session.setUserId(userBase.getUserId());
        session.setUsername(userBase.getUsername());
        session.setAddress(securityContext.getAddress());
        session.setIp(HelperLong.longIP(securityContext.getAddress(), -1));
        session.setAgent(securityContext.getAgent());
        session.setLastTime(ContextUtils.getContextTime());
        session.setPassTime(securityContext.getMaxExpirationTime());
        if (securityContext.getMetas() != null) {
            session.setMetas(KernelObject.serialize(securityContext.getMetas()));
        }

        BeanService.ME.merge(session);
    }

    @Transaction
    @Override
    public void merge(String entityName, JUser entity, MergeType mergeType, Object mergeEvent) {
        if ((mergeType == MergeType.UPDATE && !entity.isSlient()) || mergeType == MergeType.DELETE) {
            Iterator<String> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                    "SELECT o.id FROM JSession o WHERE o.userId = ? AND o.passTime > ?", entity.getUserId(),
                    ContextUtils.getContextTime()).iterate();
            while (iterator.hasNext()) {
                SecurityContext securityContext = ContextUtils.findContext(SecurityContext.class, iterator.next());
                if (securityContext != null) {
                    if (mergeType == MergeType.UPDATE) {
                        securityContext.setUser(entity);

                    } else {
                        securityContext.setExpiration();
                    }
                }
            }
        }
    }

    @Override
    public JiUserBase openUserBase(String username, String password, String platform) {
        return null;
    }
}
