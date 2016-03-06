/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月9日 下午4:11:11
 */
package com.absir.master.resolver;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.server.socket.resolver.IBufferResolver;
import com.absir.server.socket.resolver.IServerResolver;
import com.absir.server.socket.resolver.SocketSessionResolver;

/**
 * @author absir
 *
 */
@Base
@Bean
public class MasterSessionResolver extends SocketSessionResolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#getBufferResolver(
	 * )
	 */
	@Override
	public IBufferResolver getBufferResolver() {
		return MasterBufferResolver.ME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#getServerResolver(
	 * )
	 */
	@Override
	public IServerResolver getServerResolver() {
		return MasterServerResolver.ME;
	}

}
