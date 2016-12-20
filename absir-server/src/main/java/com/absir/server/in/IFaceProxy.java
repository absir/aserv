package com.absir.server.in;

import com.absir.server.on.OnPut;

/**
 * Created by absir on 2016/12/20.
 */
public interface IFaceProxy<T> {

    public T getServer(OnPut onPut);
}
