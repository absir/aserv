/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-12 下午5:32:11
 */
package com.absir.aserv.system.security;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.JbSession;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.service.SecurityService;
import com.absir.context.core.ContextBean;
import com.absir.context.core.ContextUtils;
import com.absir.property.value.Properties;
import com.absir.property.value.Property;
import com.absir.property.value.PropertyInfo;

import java.util.HashMap;
import java.util.Map;

@MaEntity(parent = {@MaMenu("在线管理")}, name = "会话")
@Properties(@Property(name = "expirationTime", infos = @PropertyInfo(value = JaLang.class, valueInfo = "过期时间")))
public class SecurityContext extends ContextBean<String> {

    @JaEdit(editable = JeEditable.DISABLE)
    @JaLang("SESSION")
    private JbSession session;

    @JaLang("变化")
    private boolean channged;

    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKNONE, listColType = 1)
    @JaLang("用户")
    private JiUserBase user;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "生命时间")
    private long lifeTime;

    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最大过期时间")
    private long maxExpirationTime;

    private Map<String, Object> metaObjects;

    public SecurityContext() {
    }

    public SecurityContext(String id) {
        setId(id);
    }

    public JbSession getSession() {
        return session;
    }

    public void setSession(JbSession session) {
        this.session = session;
    }

    public boolean isChannged() {
        return channged;
    }

    public void setChannged(boolean channged) {
        this.channged = channged;
    }

    public JiUserBase getUser() {
        return user;
    }

    public void setUser(JiUserBase user) {
        this.user = user;
    }

    @Override
    protected long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public long getMaxExpirationTime() {
        return maxExpirationTime;
    }

    public void setMaxExpirationTime(long maxExpirationTime) {
        if (maxExpirationTime == 0) {
            maxExpirationTime = -1;
        }

        this.maxExpirationTime = ContextUtils.getContextTime() + maxExpirationTime;
    }

    public Object getMetaObject(String name) {
        return metaObjects == null ? null : metaObjects.get(name);
    }

    public void removeMetaObjects(String name) {
        if (metaObjects != null) {
            metaObjects.remove(name);
        }
    }

    public void setMetaObjects(String name, Object value) {
        if (metaObjects == null) {
            synchronized (this) {
                if (metaObjects == null) {
                    metaObjects = new HashMap<String, Object>();
                }
            }
        }

        metaObjects.put(name, value);
    }

    @Override
    public boolean stepDone(long contextTime) {
        return maxExpirationTime != 0 && (maxExpirationTime < contextTime || super.stepDone(contextTime));
    }

    @Override
    protected void initialize() {
    }

    public boolean uninitializeDone() {
        return session == null || !channged || maxExpirationTime <= ContextUtils.getContextTime();
    }

    @Override
    public void uninitialize() {
        SecurityService.ME.updateSession(user, session);
    }

    public void destorySession() {
        setExpiration();
        if (session != null) {
            SecurityService.ME.deleteSession(session);
        }
    }

}
