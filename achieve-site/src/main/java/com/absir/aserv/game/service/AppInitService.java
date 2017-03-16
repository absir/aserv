package com.absir.aserv.game.service;

import com.absir.aserv.game.bean.JNotice;
import com.absir.aserv.master.service.MasterSyncService;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;

/**
 * Created by absir on 2017/2/28.
 */
@Bean
public class AppInitService {

    @Inject
    protected void initService() {
        // 同步服务
        MasterSyncService.addSyncEntityServers(JNotice.class);
    }

}
