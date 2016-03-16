/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月11日 下午3:47:00
 */
package com.absir.sockser;

import com.absir.aserv.system.bean.value.JiActive;

public interface JiServer extends JiActive {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public int getPort();

    public void setPort(int port);

    public boolean isMultiPort();

    public void setMultiPort(boolean multiPort);

    public String getIp();

    public void setIp(String ip);

    public long getBeginTime();

    public void setBeginTime(long beginTime);

    public long getPassTime();

    public void setPassTime(long passTime);

    public boolean isClosed();

    public void setClosed(boolean closed);

}
