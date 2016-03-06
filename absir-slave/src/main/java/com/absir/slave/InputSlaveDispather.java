/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月9日 下午7:48:10
 */
package com.absir.slave;

import java.nio.channels.SocketChannel;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.client.SocketAdapter;
import com.absir.core.base.Environment;
import com.absir.server.in.InDispatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.route.returned.ReturnedResolverBody;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.resolver.BodyMsgResolver;
import com.absir.slave.InputSlave.InputSlaveAtt;
import com.absir.slave.resolver.ISlaveCallback;
import com.absir.slave.resolver.SlaveBufferResolver;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputSlaveDispather extends InDispatcher<InputSlaveAtt, SocketChannel> implements ISlaveCallback {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.client.SocketAdapter.CallbackAdapte#doWith(com.absir.client.
	 * SocketAdapter, int, byte[])
	 */
	@Override
	public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
		if (buffer.length > 1) {
			InputSlaveAtt inputSocketAtt = new InputSlaveAtt(null, buffer, null, adapter);
			try {
				if (on(inputSocketAtt.getUrl(), inputSocketAtt, adapter.getSocket().getChannel())) {
					return;
				}

			} catch (Throwable e) {
				if (Environment.getEnvironment() == Environment.DEVELOP) {
					e.printStackTrace();
				}
			}

			InputSocket.writeByteBuffer(SlaveBufferResolver.ME, null, adapter.getSocket().getChannel(),
					(byte) (SocketAdapter.ERROR_FLAG | SocketAdapter.RESPONSE_FLAG), inputSocketAtt.getCallbackIndex(),
					InputSocket.NONE_RESPONSE_BYTES);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.slave.ISlaveCallback#getCallbackIndex()
	 */
	@Override
	public int getCallbackIndex() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.IDispatcher#getInMethod(java.lang.Object)
	 */
	@Override
	public InMethod getInMethod(InputSlaveAtt req) {
		return req.getMethod();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.IDispatcher#decodeUri(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public String decodeUri(String uri, InputSlaveAtt req) {
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.InDispatcher#input(java.lang.String,
	 * com.absir.server.in.InMethod, com.absir.server.in.InModel,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Input input(String uri, InMethod inMethod, InModel model, InputSlaveAtt req, SocketChannel res) {
		InputSlave input = new InputSlave(model, req, res);
		ReturnedResolverBody.ME.setBodyConverter(input, BodyMsgResolver.ME);
		return input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.in.InDispatcher#resolveReturnedValue(java.lang.Object,
	 * com.absir.server.on.OnPut)
	 */
	@Override
	public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable {
		if (onPut.getReturnValue() == null) {
			ReturnedResolver<?> returnedResolver = onPut.getReturnedResolver();
			if (returnedResolver != null && returnedResolver instanceof ReturnedResolverBody) {
				onPut.setReturnValue(InputSocket.NONE_RESPONSE);
			}
		}

		super.resolveReturnedValue(routeBean, onPut);
	}
}
