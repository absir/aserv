package com.absir.server.handler;

import com.absir.server.on.OnPut;

/**
 * Created by absir on 16/9/2.
 */
public interface IHandler {

    public boolean _permission(OnPut onPut);

    public void _finally(OnPut onPut, HandlerType.HandlerMethod method);

}
