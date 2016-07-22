package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.bean.inject.value.Bean;
import jetbrick.template.JetTemplate;

/**
 * Created by absir on 16/7/23.
 */
@Bean
public class PortalService {

    public long sendMessageCode(String email, JetTemplate template, long idleTime) {
        if (!Pag.CONFIGURE.hasMessage()) {
            return -2;
        }
    }

    public long sendEmailCode(String email, JetTemplate template, long idleTime) {
        if (!Pag.CONFIGURE.hasEmail()) {
            return -2;
        }
    }

}
