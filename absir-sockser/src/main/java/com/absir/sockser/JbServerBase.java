package com.absir.sockser;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiActive;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月11日 下午3:48:52
 */

/**
 * @author absir
 */
@MappedSuperclass
public class JbServerBase extends JbBase implements JiActive, JiServer {

    @Id
    @JaLang("纪录编号")
    private Long id;

    @JaLang("服务器名称")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String name;

    @JaLang("端口号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int port;

    @JaLang("混合端口")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean multiPort;

    @JaLang("服务器IP")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String ip;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long beginTime;

    @JaLang("关闭时间")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long passTime;

    @JaLang("关闭")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean closed;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the multiPort
     */
    public boolean isMultiPort() {
        return multiPort;
    }

    /**
     * @param multiPort the multiPort to set
     */
    public void setMultiPort(boolean multiPort) {
        this.multiPort = multiPort;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the beginTime
     */
    public long getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime the beginTime to set
     */
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the passTime
     */
    public long getPassTime() {
        return passTime;
    }

    /**
     * @param passTime the passTime to set
     */
    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    /**
     * @return the closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @param closed the closed to set
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
