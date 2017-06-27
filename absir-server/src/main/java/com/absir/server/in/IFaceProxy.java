package com.absir.server.in;

import com.absir.server.on.OnPut;

/**
 * Created by absir on 2016/12/20.
 */
public interface IFaceProxy<C, T> {

    public C getContext(OnPut onPut);

    public T getIFace(OnPut onPut, C context);

    public void doFinally(OnPut onPut, C context, Throwable e);
}
