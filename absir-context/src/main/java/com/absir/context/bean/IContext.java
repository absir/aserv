/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-11 下午4:44:15
 */
package com.absir.context.bean;

public interface IContext extends IStep {

    public void retainAt(long contextTime);

    public boolean uninitializeDone();

    public void uninitialize();
}
