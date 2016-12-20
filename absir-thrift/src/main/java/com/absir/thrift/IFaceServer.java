package com.absir.thrift;

import org.apache.thrift.TBaseProcessor;

/**
 * Created by absir on 2016/12/20.
 */
public interface IFaceServer<T> {

    public TBaseProcessor<T> getBaseProcessor();

}
