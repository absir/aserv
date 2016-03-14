/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月24日 上午11:29:27
 */
package com.absir.step;

/**
 * @author absir
 */
public interface IStep {

    /**
     * @param contextTime
     * @return
     */
    public boolean stepDone(long contextTime);

}
