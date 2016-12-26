/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午2:39:54
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaColum;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "节点", value = @MaMenu(order = -128))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JSlave extends JbBase {

    @NotEmpty
    @JaLang(value = "验证主键", tag = "verifierId")
    @Id
    private String id;

    @JaLang("名称")
    private String name;

    @JaLang("IP")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String ip;

    @JaLang("组号")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String groupId;

    @JaLang("版本")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String version;

    @JaLang("路径")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String path;

    @JaLang("应用")
    @JaColum(indexs = @Index(columnList = "app"))
    private String appCode;

    @JaLang("启动时间")
    private long startTime;

    @JaLang("连接中")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean connecting;

    @JaLang("最后连接时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long lastConnectTime;

    @JaLang("服务地址")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String serverAddress;

    @JaLang("强制开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean forceOpen;

    @JaLang("通讯密钥")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String slaveKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public long getLastConnectTime() {
        return lastConnectTime;
    }

    public void setLastConnectTime(long lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isForceOpen() {
        return forceOpen;
    }

    public void setForceOpen(boolean forceOpen) {
        this.forceOpen = forceOpen;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    public void setSlaveKey(String slaveKey) {
        this.slaveKey = slaveKey;
    }

}
