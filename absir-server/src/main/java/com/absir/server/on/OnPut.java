/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-25 上午11:05:19
 */
package com.absir.server.on;

import com.absir.binder.BinderData;
import com.absir.server.in.Input;
import com.absir.server.route.returned.ReturnedResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class OnPut {

    /**
     * On_Put_Thread_Local
     */
    static ThreadLocal<OnPut> On_Put_Thread_Local = new ThreadLocal<OnPut>();
    /**
     * input
     */
    private Input input;
    /**
     * returnValue
     */
    private Object returnValue;
    /**
     * returnedFixed
     */
    private boolean returnedFixed;
    /**
     * returned
     */
    private Object returned;
    /**
     * returnedResolver
     */
    private ReturnedResolver returnedResolver;
    /**
     * returnThrowable
     */
    private Throwable returnThrowable;
    /**
     * flagObjs
     */
    private List<Object> flagObjs;

    /**
     * @param input
     */
    public OnPut(Input input) {
        this.input = input;
    }

    /**
     * @return
     */
    public static OnPut get() {
        return On_Put_Thread_Local.get();
    }

    /**
     *
     */
    public static void close() {
        On_Put_Thread_Local.remove();
    }

    /**
     *
     */
    public void open() {
        On_Put_Thread_Local.set(this);
    }

    /**
     * @return the input
     */
    public Input getInput() {
        return input;
    }

    /**
     * @return
     */
    public BinderData getBinderData() {
        return input.getBinderData();
    }

    /**
     * @return the returnValue
     */
    public Object getReturnValue() {
        return returnValue;
    }

    /**
     * @param returnValue the returnValue to set
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * @return the returnedFixed
     */
    public boolean isReturnedFixed() {
        return returnedFixed;
    }

    /**
     * @param returnedFixed the returnedFixed to set
     */
    public void setReturnedFixed(boolean returnedFixed) {
        this.returnedFixed = returnedFixed;
    }

    /**
     * @return the returned
     */
    public Object getReturned() {
        return returned;
    }

    /**
     * @param returned the returned to set
     */
    public void setReturned(Object returned) {
        this.returned = returned;
    }

    /**
     * @return the returnedResolver
     */
    public ReturnedResolver getReturnedResolver() {
        return returnedResolver;
    }

    /**
     * @param returnedResolver the returnedResolver to set
     */
    public void setReturnedResolver(ReturnedResolver returnedResolver) {
        this.returnedResolver = returnedResolver;
    }

    /**
     * @return the returnThrowable
     */
    public Throwable getReturnThrowable() {
        return returnThrowable;
    }

    /**
     * @param returnThrowable the returnThrowable to set
     */
    public void setReturnThrowable(Throwable returnThrowable) {
        this.returnThrowable = returnThrowable;
    }

    /**
     * @param flagObj
     */
    public void addFlagObj(Object flagObj) {
        if (flagObjs == null) {
            flagObjs = new ArrayList<Object>();
        }

        flagObjs.add(flagObj);
    }

    /**
     * @param flagObj
     * @return
     */
    public boolean isContainFlagObj(Object flagObj) {
        return flagObjs == null ? false : flagObjs.contains(flagObj);
    }
}
