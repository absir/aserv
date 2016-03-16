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
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.context.core.ContextBean;
import com.absir.context.core.ContextUtils;
import com.absir.property.value.Properties;
import com.absir.property.value.Property;
import com.absir.property.value.PropertyInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@MaEntity(parent = {@MaMenu("在线管理")}, name = "会话")
@Properties(@Property(name = "expirationTime", infos = @PropertyInfo(value = JaLang.class, valueInfo = "过期时间")))
public class SecurityContext extends ContextBean<String> {

    @JaEdit(editable = JeEditable.DISABLE)
    private ISecuritySupply securitySupply;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户")
    private JiUserBase user;

    @JaEdit(types = "ip", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "IP地址", tag = "ip")
    private String address;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "设备", tag = "device")
    private String agent;

    private Map<String, Serializable> metas;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "生命时间")
    private long lifeTime;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最大过期时间")
    private long maxExpirationTime;

    private Map<String, Object> metaObjects;

    public ISecuritySupply getSecuritySupply() {
        return securitySupply;
    }

    public void setSecuritySupply(ISecuritySupply securitySupply) {
        this.securitySupply = securitySupply;
    }

    public JiUserBase getUser() {
        return user;
    }

    public void setUser(JiUserBase user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Map<String, Serializable> getMetas() {
        return metas;
    }

    public void setMetas(Map<String, Serializable> metas) {
        this.metas = metas;
    }

    public Serializable getMeta(String name) {
        return metas == null ? null : metas.get(name);
    }

    public void removeMeta(String name) {
        if (metas != null) {
            metas.remove(name);
        }
    }

    public void setMeta(String name, Serializable value) {
        if (metas == null) {
            synchronized (this) {
                if (metas == null) {
                    metas = new HashMap<String, Serializable>();
                }
            }
        }

        metas.put(name, value);
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
        return securitySupply == null || user == null || maxExpirationTime <= ContextUtils.getContextTime();
    }

    @Override
    public void uninitialize() {
        securitySupply.saveSession(this);
    }

    public void destorySession() {
        setSecuritySupply(null);
        setExpiration();
    }
}
