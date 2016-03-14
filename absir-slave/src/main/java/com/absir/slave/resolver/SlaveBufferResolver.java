/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月9日 下午8:28:33
 */
package com.absir.slave.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.server.socket.resolver.SocketBufferResolver;

/**
 * @author absir
 */
@Base
@Bean
public class SlaveBufferResolver extends SocketBufferResolver {

    /**
     * ME
     */
    public static final SlaveBufferResolver ME = BeanFactoryUtils.get(SlaveBufferResolver.class);

}
