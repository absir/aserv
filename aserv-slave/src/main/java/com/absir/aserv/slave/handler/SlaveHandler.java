package com.absir.aserv.slave.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.client.rpc.RpcData;
import com.absir.server.handler.HandlerType;
import com.absir.server.handler.IHandler;
import com.absir.server.on.OnPut;
import com.absir.server.value.Handler;
import com.absir.shared.bean.SlaveStatus;
import com.absir.shared.slave.ISlave;
import com.absir.slave.InputSlave;

/**
 * Created by absir on 2016/10/27.
 */
@Base
@Bean
@Handler
public class SlaveHandler implements IHandler, ISlave {

    public static final SlaveHandler ME = BeanFactoryUtils.get(SlaveHandler.class);

    @Override
    public boolean _permission(OnPut onPut) {
        return InputSlave.onAuthentication(onPut.getInput());
    }

    @Override
    public void _finally(OnPut onPut, HandlerType.HandlerMethod method) {
    }

    @Override
    public long time() {
        return System.currentTimeMillis();
    }

    @Override
    public RpcData readyUpgrade(SlaveStatus slaveStatus) {
        return null;
    }

    @Override
    public void doUpgrade(SlaveStatus slaveStatus) {
    }

}
