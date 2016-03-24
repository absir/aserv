package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by absir on 16/3/23.
 */
@MaEntity(parent = {@MaMenu("节点管理")}, name = "节点", value = @MaMenu(order = -128))
@Entity
public class JSlave extends JbBean {

    @JaLang("名称")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    private String name;

    @JaLang("地址")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    @Column(unique = true)
    private String address;

    @JaLang("连接中")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean connecting;

    @JaLang("最后连接时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long lastConnectTime;

    @JaLang("组号")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String groupId;

    @JaLang("版本")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String version;

    @JaLang("IP")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String ip;

    @JaLang("服务IP")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String serverIP;

    @JaLang("强制开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean forceOpen;

    @JaLang("路径")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String path;

    @JaLang("通讯密钥")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String slaveKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
