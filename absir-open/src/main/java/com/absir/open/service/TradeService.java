/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月28日 下午3:19:51
 */
package com.absir.open.service;

import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Domain;
import com.absir.context.core.ContextUtils;
import com.absir.open.bean.JPayHistory;
import com.absir.open.bean.JPayTrade;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

@Base
@Bean
public class TradeService {

    public static final TradeService ME = BeanFactoryUtils.get(TradeService.class);

    @Domain
    private DSequence sessionSequence;

    public String nextTradeId(int hashCode) {
        return sessionSequence.getNextId();
    }

    @Transaction
    public boolean addPayHistory(JPayTrade payTrade) {
        String tradeNo = payTrade.getPlatform() + "@" + payTrade.getTradeNo();
        Session session = BeanDao.getSession();
        if (QueryDaoUtils.createQueryArray(session,
                "SELECT o FROM JPayHistory o WHERE o.tradeNo = ?", tradeNo).iterate()
                .hasNext()) {
            return false;
        }

        JPayHistory payHistory = new JPayHistory();
        payHistory.setId(payTrade.getId());
        payHistory.setTradeNo(tradeNo);
        payHistory.setCreateTime(ContextUtils.getContextTime());
        try {
            session.persist(payHistory);

        } catch (ConstraintViolationException e) {
            return false;
        }

        return true;
    }

}
