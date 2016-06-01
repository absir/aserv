package com.absir.dubbo.service.client;

import com.absir.aserv.system.bean.value.JaCrud;

/**
 * Created by absir on 16/5/30.
 */
public interface ReceiverService {

    public void crud(JaCrud.Crud crud, String entityName, byte[] entityBytes);

    public void mergeConfigure(String name, byte[] configureBytes);



}
