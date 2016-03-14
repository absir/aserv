/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年8月28日 下午3:19:51
 */
package com.absir.open.service;

import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.open.bean.JPayHistory;
import com.absir.open.bean.JPayTrade;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
@Base
@Bean
public class TradeService {

    /**
     * ME
     */
    public static final TradeService ME = BeanFactoryUtils.get(TradeService.class);

    /**
     * tokenMap
     */
    protected Map<String, Object> tokenMap = new HashMap<String, Object>();

    /**
     * @param hashCode
     * @return
     */
    public String newTradeId(int hashCode) {
        return HelperRandom.randSecendId(hashCode);
    }

    /**
     * @param payTrade
     * @param tradeNo
     */
    @Transaction
    public boolean addPayHistory(JPayTrade payTrade, String tradeNo) {
        String platform = payTrade.getPlatform();
        Session session = BeanDao.getSession();
        if (QueryDaoUtils.createQueryArray(session,
                "SELECT o FROM JPayHistory o WHERE o.platform = ? and o.tradeNo = ?", platform, tradeNo).iterate()
                .hasNext()) {
            return false;
        }

        JPayHistory payHistory = new JPayHistory();
        payHistory.setId(payTrade.getId());
        payHistory.setPlatform(platform);
        payHistory.setTradeNo(tradeNo);
        session.persist(payHistory);
        return true;
    }

}
