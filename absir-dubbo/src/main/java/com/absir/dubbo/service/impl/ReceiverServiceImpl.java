package com.absir.dubbo.service.impl;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.dubbo.service.client.ReceiverService;
import com.alibaba.dubbo.config.annotation.Service;

/**
 * Created by absir on 16/5/30.
 */
@Service
public class ReceiverServiceImpl implements ReceiverService {


    @Override
    public void crud(JaCrud.Crud crud, String entityName, byte[] entityBytes) {


    }

    @Override
    public void mergeConfigure(String name, byte[] configureBytes) {

    }
}
