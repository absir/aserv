/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月13日 下午4:14:46
 */
package com.absir.aserv.slave.api;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.slave.bean.JServer;
import com.absir.aserv.slave.bean.dto.DUpgradeSlave;
import com.absir.aserv.slave.service.SlaveUpgradeService;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.CrudService;
import com.absir.bean.basis.Base;
import com.absir.data.helper.HelperDataFormat;
import com.absir.server.value.Body;
import com.absir.server.value.Server;

import java.io.IOException;

@Base
@Server
public class api_slave extends ApiSlave {

    @JaLang("时间")
    public long time() {
        return System.currentTimeMillis();
    }

    @JaLang("同步服务")
    public void sync(@Body JServer server) {
        BeanService.ME.merge(server);
    }

    @JaLang("同步服务实体")
    public void merge(String entityName, @Body byte[] postData) throws IOException {
        option(entityName, 0, postData);
    }

    @JaLang("同步服务实体")
    public void option(String entityName, int option, @Body byte[] postData) throws IOException {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        Class<?> entityClass = crudSupply.getEntityClass(entityName);
        Object entity = HelperDataFormat.PACK.read(postData, 0, postData.length, entityClass);
        if (option == 0) {
            crudSupply.mergeEntity(entityName, entity, false);

        } else if (option == 1) {
            crudSupply.mergeEntity(entityName, entity, true);

        } else {
            crudSupply.deleteEntity(entityName, entity);
        }
    }

    @JaLang("版本升级")
    public void upgrade(@com.absir.server.value.Body DUpgradeSlave upgradeSlave) throws Exception {
        SlaveUpgradeService.ME.addDUpgradeSlave(upgradeSlave);
    }
}
