/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午2:51:49
 */
package com.absir.aserv.master.service;

import com.absir.aserv.master.bean.JSlaveRegister;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelString;
import com.absir.master.resolver.MasterServerResolver;
import com.absir.server.socket.SelSession;

import java.nio.channels.SocketChannel;

@Base
@Bean
public class MasterResolverService extends MasterServerResolver {

    @Override
    public String idForMaster(String[] params, SocketChannel socketChannel, SelSession selSession) {
        String id = super.idForMaster(params, socketChannel, selSession);
        JSlaveRegister register = BeanService.ME.get(JSlaveRegister.class, id);
        if (register == null) {
            register = new JSlaveRegister();
            register.setId(id);
            BeanService.ME.merge(register);
        }

        if (register.isAllow()) {
            id = register.getId();
            return KernelString.isEmpty(id) ? null : id;
        }

        return null;
    }
}
