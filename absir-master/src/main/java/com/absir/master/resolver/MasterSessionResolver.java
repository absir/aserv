/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月9日 下午4:11:11
 */
package com.absir.master.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.server.socket.resolver.IBufferResolver;
import com.absir.server.socket.resolver.IServerResolver;
import com.absir.server.socket.resolver.SocketSessionResolver;

@Base
@Bean
public class MasterSessionResolver extends SocketSessionResolver {

    @Override
    public IBufferResolver getBufferResolver() {
        return MasterBufferResolver.ME;
    }

    @Override
    public IServerResolver getServerResolver() {
        return MasterServerResolver.ME;
    }

}
