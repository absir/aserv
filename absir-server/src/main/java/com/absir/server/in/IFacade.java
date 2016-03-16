/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年8月27日 上午10:11:20
 */
package com.absir.server.in;

public interface IFacade {

    public String getAddress();

    public Integer getLocaleCode();

    public Object getSession(String name);

    public String getSessionValue(String name);

    public void setSession(String name, Object value);

    public void removeSession(String name);

    public String getCookie(String name);

    public void setCookie(String name, String value, String path, long remember);

    public void removeCookie(String name, String path);

}
