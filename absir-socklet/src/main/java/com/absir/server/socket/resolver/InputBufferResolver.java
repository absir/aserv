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

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputBufferResolver extends SocketBufferResolver {

    /**
     * ME
     */
    public static final InputBufferResolver ME = BeanFactoryUtils.get(InputBufferResolver.class);

}
