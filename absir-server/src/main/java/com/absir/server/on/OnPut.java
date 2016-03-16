/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 上午11:05:19
 */
package com.absir.server.on;

import com.absir.binder.BinderData;
import com.absir.server.in.Input;
import com.absir.server.route.returned.ReturnedResolver;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class OnPut {

    static ThreadLocal<OnPut> On_Put_Thread_Local = new ThreadLocal<OnPut>();

    private Input input;

    private Object returnValue;

    private boolean returnedFixed;

    private Object returned;

    private ReturnedResolver returnedResolver;

    private Throwable returnThrowable;

    private List<Object> flagObjs;

    public OnPut(Input input) {
        this.input = input;
    }

    public static OnPut get() {
        return On_Put_Thread_Local.get();
    }

    public static Input input() {
        OnPut put = get();
        return put == null ? null : put.getInput();
    }

    public static void close() {
        On_Put_Thread_Local.remove();
    }

    public void open() {
        On_Put_Thread_Local.set(this);
    }

    public Input getInput() {
        return input;
    }

    public BinderData getBinderData() {
        return input.getBinderData();
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public boolean isReturnedFixed() {
        return returnedFixed;
    }

    public void setReturnedFixed(boolean returnedFixed) {
        this.returnedFixed = returnedFixed;
    }

    public Object getReturned() {
        return returned;
    }

    public void setReturned(Object returned) {
        this.returned = returned;
    }

    public ReturnedResolver getReturnedResolver() {
        return returnedResolver;
    }

    public void setReturnedResolver(ReturnedResolver returnedResolver) {
        this.returnedResolver = returnedResolver;
    }

    public Throwable getReturnThrowable() {
        return returnThrowable;
    }

    public void setReturnThrowable(Throwable returnThrowable) {
        this.returnThrowable = returnThrowable;
    }

    public void addFlagObj(Object flagObj) {
        if (flagObjs == null) {
            flagObjs = new ArrayList<Object>();
        }

        flagObjs.add(flagObj);
    }

    public boolean isContainFlagObj(Object flagObj) {
        return flagObjs == null ? false : flagObjs.contains(flagObj);
    }
}
