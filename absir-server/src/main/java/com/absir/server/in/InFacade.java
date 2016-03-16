package com.absir.server.in;

/**
 * Created by absir on 16/3/16.
 */
public abstract class InFacade<T> implements IFacade {

    protected T input;

    protected void setInput(T input) {
        this.input = input;
    }

    public String getSessionValue(String name) {
        Object value = getSession(name);
        return value == null ? null : value.toString();
    }
}
