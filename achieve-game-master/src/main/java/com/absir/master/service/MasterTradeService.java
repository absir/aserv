/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月19日 下午2:27:17
 */
package com.absir.master.service;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.master.service.MasterSlaveService;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.EntityService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilContext;
import com.absir.open.bean.JPayTrade;
import com.absir.open.bean.value.JePayStatus;
import com.absir.open.service.IPayProccessor;
import com.absir.open.service.TradeService;
import com.absir.open.service.utils.PayUtils;
import com.absir.orm.transaction.value.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author absir
 */
@Base
@Bean
public class MasterTradeService implements IPayProccessor {

    /**
     * ME
     */
    public static final MasterTradeService ME = BeanFactoryUtils.get(MasterTradeService.class);

    /**
     * LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(MasterTradeService.class);

    /**
     * 生成购买订单
     *
     * @param platform
     * @param channel
     * @param serverId
     * @param playerId
     * @param index
     * @param amount
     * @return
     */
    @Transaction
    public JPayTrade buyDollar(String platform, String channel, long serverId, long playerId, int index, float amount) {
        String value = EntityService.ME.getDictCache().getCacheValue(platform + "@amount");
        if (value != null) {
            amount = KernelDyna.toInteger(value, 0);
        }

        JPayTrade payTrade = new JPayTrade();
        payTrade.setId(TradeService.ME.newTradeId((int) playerId));
        payTrade.setCreateTime(UtilContext.getCurrentTime());
        payTrade.setPlatform(platform);
        payTrade.setPlatformData(EntityService.ME.getDictCache().getCacheValue(platform + '@' + index));
        payTrade.setChannel(channel);
        payTrade.setAmount(amount);
        payTrade.setServerId(serverId);
        payTrade.setUserId(playerId);
        payTrade.setName("JDollar");
        payTrade.setNameId(index);
        BeanDao.getSession().persist(payTrade);
        return payTrade;
    }

    /*
     * 充值通知
     *
     * (non-Javadoc)
     *
     * @see com.absir.open.service.IPayProccessor#proccess(com.absir.open.bean.
     * JPayTrade)
     */
    @Override
    public Object proccess(JPayTrade payTrade) throws Exception {
        if ("JDollar".equals(payTrade.getName())) {
            long serverId = payTrade.getServerId();
            JSlaveServer slaveServer = BeanService.ME.get(JSlaveServer.class, serverId);
            if (slaveServer != null) {
                long playerId = payTrade.getUserId();
                int index = payTrade.getNameId();
                MasterSlaveService.ME.addSlaveSynch(slaveServer.getHost().getId(), "notifyPay" + payTrade.getId(),
                        "api/command/topup/" + playerId + "/" + index + "/" + payTrade.getAmount() + "/"
                                + payTrade.getPlatform(),
                        null);
                return playerId;
            }
        }

        return null;
    }

    /**
     * @param platform
     * @param channel
     * @param serverId
     * @param playerId
     * @param index
     * @param amount
     * @param tradeNo
     * @return
     * @throws Exception
     */
    @Transaction
    public Object buyValidate(String platform, String channel, long serverId, long playerId, int index, float amount,
                              String tradeNo) throws Exception {
        JPayTrade payTrade = (JPayTrade) BeanService.ME.selectQuerySingle(
                "SELECT o FROM JPayTrade o WHERE o.platform = ? AND o.tradeNo = ?", platform, tradeNo);
        if (payTrade == null) {
            payTrade = buyDollar(platform, channel, serverId, playerId, index, amount);
        }

        return PayUtils.proccess(platform, payTrade);
    }

    /**
     * 删除过期订单
     */
    @Async(notifier = true)
    @Schedule(cron = "0 0 0 * * *")
    @Transaction
    protected void passPayTrade() {
        long passTime = UtilContext.getCurrentTime() - UtilAbsir.WEEK_TIME * 4;
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "DELETE FROM JPayTrade o WHERE o.status != ? AND o.createTime < ? ", JePayStatus.COMPLETE, passTime)
                .executeUpdate();
    }

}
