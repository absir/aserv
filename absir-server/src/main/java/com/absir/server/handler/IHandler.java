package com.absir.server.handler;

import com.absir.server.on.OnPut;
import org.omg.CORBA.Object;

/**
 * Created by absir on 16/9/2.
 */
public interface IHandler {

    public boolean _before(OnPut onPut);

    public Object _after(OnPut onPut, HandlerType.HandlerMethod method, boolean invoked, Object returnValue, Exception exception);

}
