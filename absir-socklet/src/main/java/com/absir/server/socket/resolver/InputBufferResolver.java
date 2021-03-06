/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月4日 下午4:20:27
 */
package com.absir.server.socket.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;

@Base
@Bean
public class InputBufferResolver extends SocketBufferResolver {

    public static final InputBufferResolver ME;

    static {
        InputBufferResolver resolver = BeanFactoryUtils.get(InputBufferResolver.class);
        ME = resolver == null ? new InputBufferResolver() : resolver;
    }

}
