/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午2:31:03
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.kernel.KernelString;
import com.absir.orm.value.JaColum;
import com.absir.server.in.Input;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "服务", value = @MaMenu(order = -125))
@JaModel(desc = true)
@Entity
public class JSlaveServer extends JbBean implements ICrudBean {

    @JaLang(value = "服务名称", tag = "serverName")
    private String name;

    @JaLang("节点")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ManyToOne(fetch = FetchType.LAZY)
    private JSlave slave;

    @JaLang("端口号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int port;

    @JaLang("混合端口")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean multiPort;

    @JaLang(value = "IP")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String ip;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang("关闭时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long passTime;

    @JaLang("更新时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime", editable = JeEditable.LOCKED)
    private long updateTime;

    @JaLang("服务地址")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String serverAddress;

    @JaLang("资源链接")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String resourceUrl;

    @JaLang("关闭")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean closed;

    @JaLang("已经同步")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    @JaColum(indexs = @Index(columnList = "synched"))
    private boolean synched;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSlave getSlave() {
        return slave;
    }

    public void setSlave(JSlave slave) {
        this.slave = slave;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isMultiPort() {
        return multiPort;
    }

    public void setMultiPort(boolean multiPort) {
        this.multiPort = multiPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isSynched() {
        return synched;
    }

    public void setSynched(boolean synched) {
        this.synched = synched;
    }

    @Override
    public void processCrud(Crud crud, CrudHandler handler, Input input) {
        if (crud == Crud.CREATE && slave != null) {
            if (KernelString.isEmpty(serverAddress)) {
                serverAddress = slave.getServerAddress();
                if (KernelString.isEmpty(serverAddress)) {
                    serverAddress = slave.getIp();
                }

            } else if ("*".equals(serverAddress)) {
                serverAddress = null;
            }

            if (port == 0) {
                Integer portInteger = (Integer) BeanService.ME
                        .selectQuerySingle("SELECT MAX(o.port) FROM JSlaveServer o WHERE o.host.id = ?", slave.getId());
                port = portInteger == null ? 18891 : (portInteger + 1);
            }
        }
    }

}
