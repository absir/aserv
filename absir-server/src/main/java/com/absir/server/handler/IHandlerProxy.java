package com.absir.server.handler;

import com.absir.server.on.OnPut;

/**
 * Created by absir on 2016/10/27.
 */
public interface IHandlerProxy<T extends IHandler> extends IHandler {

    public Class<T> getInterface();

    public T getHandler(OnPut onPut);
}
