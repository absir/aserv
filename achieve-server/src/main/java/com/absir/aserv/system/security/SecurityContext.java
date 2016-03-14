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

/**
 * @author absir
 *
 */
@MaEntity(parent = {@MaMenu("在线管理")}, name = "会话")
@Properties(@Property(name = "expirationTime", infos = @PropertyInfo(value = JaLang.class, valueInfo = "过期时间")))
public class SecurityContext extends ContextBean<String> {

    /**
     * securitySupply
     */
    @JaEdit(editable = JeEditable.DISABLE)
    private ISecuritySupply securitySupply;

    /**
     * user
     */
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户")
    private JiUserBase user;

    /**
     * address
     */
    @JaEdit(types = "ip", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "IP地址", tag = "ip")
    private String address;

    /**
     * agent
     */
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "设备", tag = "device")
    private String agent;

    /** metas */
    private Map<String, Serializable> metas;

    /**
     * lifeTime
     */
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "生命时间")
    private long lifeTime;

    /**
     * maxExpirationTime
     */
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最大过期时间")
    private long maxExpirationTime;

    /**
     * metaObjects
     */
    private Map<String, Object> metaObjects;

    /**
     * @return the securitySupply
     */
    public ISecuritySupply getSecuritySupply() {
        return securitySupply;
    }

    /**
     * @param securitySupply the securitySupply to set
     */
    public void setSecuritySupply(ISecuritySupply securitySupply) {
        this.securitySupply = securitySupply;
    }

    /**
     * @return the user
     */
    public JiUserBase getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(JiUserBase user) {
        this.user = user;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the agent
     */
    public String getAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * @return the metas
     */
    public Map<String, Serializable> getMetas() {
        return metas;
    }

    /**
     * @param metas the metas to set
     */
    public void setMetas(Map<String, Serializable> metas) {
        this.metas = metas;
    }

    /**
     * @param name
     * @return
     */
    public Serializable getMeta(String name) {
        return metas == null ? null : metas.get(name);
    }

    /**
     * @param name
     */
    public void removeMeta(String name) {
        if (metas != null) {
            metas.remove(name);
        }
    }

    /**
     * @return the meta
     */
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

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.ContextBean#getLifeTime()
     */
    @Override
    protected long getLifeTime() {
        return lifeTime;
    }

    /**
     * @param lifeTime
     *            the lifeTime to set
     */
    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * @return the maxExpirationTime
     */
    public long getMaxExpirationTime() {
        return maxExpirationTime;
    }

    /**
     * @param maxExpirationTime
     *            the maxExpirationTime to set
     */
    public void setMaxExpirationTime(long maxExpirationTime) {
        if (maxExpirationTime == 0) {
            maxExpirationTime = -1;
        }

        this.maxExpirationTime = ContextUtils.getContextTime() + maxExpirationTime;
    }

    /**
     * @param name
     * @return
     */
    public Object getMetaObject(String name) {
        return metaObjects == null ? null : metaObjects.get(name);
    }

    /**
     * @param name
     */
    public void removeMetaObjects(String name) {
        if (metaObjects != null) {
            metaObjects.remove(name);
        }
    }

    /**
     * @return the meta
     */
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

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.context.IContext#stepDone(long)
     */
    @Override
    public boolean stepDone(long contextTime) {
        return maxExpirationTime != 0 && (maxExpirationTime < contextTime || super.stepDone(contextTime));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.Context#initialize()
     */
    @Override
    protected void initialize() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.ContextBean#uninitializeDone()
     */
    public boolean uninitializeDone() {
        return securitySupply == null || user == null || maxExpirationTime <= ContextUtils.getContextTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.ContextBean#uninitialize()
     */
    @Override
    public void uninitialize() {
        securitySupply.saveSession(this);
    }
}
