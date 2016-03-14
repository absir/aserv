/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月11日 下午3:47:00
 */
package com.absir.sockser;

import com.absir.aserv.system.bean.value.JiActive;

/**
 * @author absir
 */
public interface JiServer extends JiActive {

    /**
     * @return the id
     */
    public Long getId();

    /**
     * @param id the id to set
     */
    public void setId(Long id);

    /**
     * @return the name
     */
    public String getName();

    /**
     * @param name the name to set
     */
    public void setName(String name);

    /**
     * @return the port
     */
    public int getPort();

    /**
     * @param port the port to set
     */
    public void setPort(int port);

    /**
     * @return the multiPort
     */
    public boolean isMultiPort();

    /**
     * @param multiPort the multiPort to set
     */
    public void setMultiPort(boolean multiPort);

    /**
     * @return the ip
     */
    public String getIp();

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip);

    /**
     * @return the beginTime
     */
    public long getBeginTime();

    /**
     * @param beginTime the beginTime to set
     */
    public void setBeginTime(long beginTime);

    /**
     * @return the passTime
     */
    public long getPassTime();

    /**
     * @param passTime the passTime to set
     */
    public void setPassTime(long passTime);

    /**
     * @return the closed
     */
    public boolean isClosed();

    /**
     * @param closed the closed to set
     */
    public void setClosed(boolean closed);

}
