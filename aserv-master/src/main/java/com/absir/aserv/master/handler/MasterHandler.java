package com.absir.aserv.master.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.master.IMaster;
import com.absir.master.InputMaster;
import com.absir.server.handler.HandlerType;
import com.absir.server.handler.IHandler;
import com.absir.server.on.OnPut;
import com.absir.server.value.Handler;

/**
 * Created by absir on 2016/10/27.
 */
@Base
@Bean
@Handler
public class MasterHandler implements IHandler, IMaster {

    @Override
    public boolean _permission(OnPut onPut) {
        return InputMaster.onAuthentication(onPut.getInput());
    }

    @Override
    public void _finally(OnPut onPut, HandlerType.HandlerMethod method) {
    }

    @Override
    public long time() {
        return System.currentTimeMillis();
    }

}
