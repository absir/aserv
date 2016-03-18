/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年1月4日 上午9:47:43
 */
package com.absir.aserv.system.bean.value;

public interface IUser {

    public String getPassword();

    public void setPassword(String password);

    public String getSalt();

    public void setSalt(String salt);

    public int getSaltCount();

    public void setSaltCount(int saltCount);

    public long getLastLogin();

    public void setLastLogin(long lastLogin);

    public int getErrorLogin();

    public void setErrorLogin(int errorLogin);

    public long getLastErrorLogin();

    public void setLastErrorLogin(long lastErrorLogin);

}
