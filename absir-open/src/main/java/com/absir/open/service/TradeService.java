/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月28日 下午3:19:51
 */
package com.absir.open.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Domain;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;
import com.absir.open.bean.JPayConfigure;
import com.absir.open.bean.JPayHistory;
import com.absir.open.bean.JPayTrade;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

@Base
@Bean
public class TradeService {

    public static final TradeService ME = BeanFactoryUtils.get(TradeService.class);

    public static final JPayConfigure PAY_CONFIGURE = JConfigureUtils.getConfigure(JPayConfigure.class);

    @Domain
    protected DSequence tradeSequence;

    public String nextTradeId() {
        return tradeSequence.getNextId();
    }

    public String nextShortTradeId() {
        return tradeSequence.getNextDigLetterId();
    }

    @Transaction
    public boolean addPayHistory(JPayTrade payTrade) {
        String tradeId = payTrade.getId();
        String tradeNo = KernelString.isEmpty(payTrade.getTradeNo()) ? null : (payTrade.getPlatform() + "@" + payTrade.getTradeNo());
        Session session = BeanDao.getSession();
        if (tradeNo == null) {
            if (QueryDaoUtils.createQueryArray(session,
                    "SELECT o.id FROM JPayHistory o WHERE o.id = ?", tradeId).iterate()
                    .hasNext()) {
                return false;
            }

        } else {
            if (QueryDaoUtils.createQueryArray(session,
                    "SELECT o.id FROM JPayHistory o WHERE o.id = ? OR o.tradeNo = ?", tradeId, tradeNo).iterate()
                    .hasNext()) {
                return false;
            }
        }

        JPayHistory payHistory = new JPayHistory();
        payHistory.setId(tradeId);
        payHistory.setTradeNo(tradeNo);
        payHistory.setCreateTime(ContextUtils.getContextShortTime());
        try {
            session.persist(payHistory);

        } catch (RuntimeException e) {
            SessionFactoryUtils.throwNoConstraintViolationException(e);
            return false;
        }

        return true;
    }

}
