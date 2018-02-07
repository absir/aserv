package com.absir.aserv.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.bean.core.BeanFactoryUtils;

import java.io.IOException;

public interface IConsistent {

    public static final IConsistent ME = BeanFactoryUtils.get(IConsistent.class);

    public void pubConfigure(JConfigureBase configureBase) throws IOException;

}
