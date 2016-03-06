/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月13日 下午2:51:49
 */
package com.absir.aserv.master.service;

import java.nio.channels.SocketChannel;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.master.resolver.MasterServerResolver;
import com.absir.server.socket.SelSession;

/**
 * @author absir
 *
 */
@Base
@Bean
public class MasterResolverService extends MasterServerResolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.master.resolver.MasterServerResolver#idForMaster(java.lang.
	 * String[], java.nio.channels.SocketChannel,
	 * com.absir.server.socket.SelSession)
	 */
	@Override
	public String idForMaster(String[] params, SocketChannel socketChannel, SelSession selSession) {
		return super.idForMaster(params, socketChannel, selSession);
	}
}
