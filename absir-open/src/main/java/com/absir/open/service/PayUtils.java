/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-14 上午10:04:28
 */
package com.absir.open.service;

import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelString;
import com.absir.open.bean.JPayTrade;
import com.absir.open.bean.value.JePayStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Configure
public abstract class PayUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PayUtils.class);

    @Inject(type = InjectType.Selectable)
    private static IPayProcessor payProcessor;

    @Inject(value = "PayInterface", type = InjectType.Selectable)
    private static Map<String, IPayInterface> payInterfaceMap;

    public static JPayTrade createTrade(int configureId, String platform, String platformData, String channel, String goodsId, int goodsNumber, float amount, long userId, long serverId, long playerId, boolean shortTradeId, String[] moreDatas) {
        JPayTrade payTrade = new JPayTrade();
        payTrade.setConfigureId(configureId);
        payTrade.setPlatform(platform);
        payTrade.setPlatformData(platformData);
        payTrade.setChannel(channel);
        payTrade.setGoodsId(goodsId);
        payTrade.setGoodsNumber(goodsNumber);
        payTrade.setAmount(amount);
        payTrade.setUserId(userId);
        payTrade.setServerId(serverId);
        payTrade.setPlayerId(playerId);
        payProcessor.order(payTrade, shortTradeId, moreDatas);
        if (payTrade.getId() == null) {
            payTrade.setId(shortTradeId ? TradeService.ME.nextShortTradeId() : TradeService.ME.nextTradeId());
        }

        return payTrade;
    }

    protected static <T> T getPayConfigure(IPayInterface<T> payInterface, String platform, int configureId) {
        if (configureId == 0) {
            return payInterface.getConfigure();
        }

        return null;
    }

    public static String orderTrade(JPayTrade payTrade, String prepare, String[] moreDatas)
            throws Exception {
        if (payInterfaceMap != null) {
            String platform = payTrade.getPlatform();
            if (!KernelString.isEmpty(platform)) {
                IPayInterface payInterface = payInterfaceMap.get(platform);
                if (payInterface != null) {
                    payTrade.setPlatform(platform);
                    return payInterface.order(getPayConfigure(payInterface, platform, payTrade.getConfigureId()), payTrade, prepare, moreDatas);
                }
            }
        }

        return null;
    }

    // return unique tradeId
    public static String validator(int configureId, String platform, String platformData, String tradeNo, String tradeReceipt, String goodsId, int goodsNumber, float amount, boolean sandbox, String... moreDatas) throws Exception {
        if (payInterfaceMap != null && !KernelString.isEmpty(platform)) {
            IPayInterface<Object> payInterface = payInterfaceMap.get(platform);
            if (payInterface != null) {
                Object configure = getPayConfigure(payInterface, platform, configureId);
                if (configure != null) {
                    return payInterface.validator(configure, platformData, tradeNo, tradeReceipt, goodsId, goodsNumber, amount, sandbox, moreDatas);
                }
            }
        }

        return null;
    }

    public static String validator(JPayTrade payTrade) throws Exception {
        JePayStatus status = payTrade.getStatus();
        if (status == null || status.compareTo(JePayStatus.ERROR) <= 0) {
            return validator(payTrade.getConfigureId(), payTrade.getPlatform(), payTrade.getPlatformData(), payTrade.getTradeNo(), payTrade.getTradeReceipt(), payTrade.getGoodsId(), payTrade.getGoodsNumber(), payTrade.getAmount(), payTrade.isSandbox(), payTrade.getMoreDatas());
        }

        return null;
    }

    public static Object payStatus(JPayTrade payTrade, int configureId, String platform, String platformData, String tradeNo, String tradeReceipt, float amount, boolean sandbox, String[] moreDatas, JePayStatus payStatus,
                                   String statusData) {
        if (payTrade.getStatus() == JePayStatus.COMPLETE) {
            return null;
        }

        if (configureId >= 0) {
            payTrade.setConfigureId(configureId);
        }

        if (platform == null) {
            platform = payTrade.getPlatform();

        } else {
            payTrade.setPlatform(platform);
        }

        if (platformData != null) {
            payTrade.setPlatform(platformData);
        }

        payTrade.setTradeNo(tradeNo);
        payTrade.setTradeReceipt(tradeReceipt);
        payTrade.setSandbox(sandbox);
        if (moreDatas != null) {
            payTrade.setMoreDatas(moreDatas);
        }

        payTrade.setStatusData(statusData);
        Object result = null;
        if (payStatus != JePayStatus.COMPLETE) {
            if (payStatus == JePayStatus.PAYING || payStatus == JePayStatus.PAYED) {
                if (payStatus == JePayStatus.PAYED) {
                    if (amount >= 0 && amount < payTrade.getAmount()) {
                        payStatus = JePayStatus.PAYING;
                    }
                }

                try {
                    if (payStatus != JePayStatus.PAYED) {
                        String receiptId = validator(payTrade);
                        if (receiptId != null) {
                            if (receiptId.length() > 0) {
                                payTrade.setTradeNo(receiptId);
                            }

                            payStatus = JePayStatus.PAYED;
                        }
                    }

                    if (payStatus == JePayStatus.PAYED) {
                        if (amount > 0) {
                            payTrade.setAmount(amount);
                        }

                        return processDone(platform, payTrade);
                    }

                } catch (Exception e) {
                    LOGGER.error("payStatus error => " + payTrade.getId(), e);
                }

            } else if (payStatus != payTrade.getStatus()) {
                payTrade.setStatus(payStatus);
                result = Boolean.TRUE;
            }

        } else {
            payTrade.setStatus(payStatus);
        }

        BeanService.ME.merge(payTrade);
        return result;
    }

    public static Object processDone(String platform, JPayTrade payTrade) {
        if (payProcessor != null) {
            try {
                if (platform != null) {
                    payTrade.setPlatform(platform);
                }

                if (TradeService.ME.addPayHistory(payTrade)) {
                    Object result = payProcessor.process(payTrade);
                    if (payTrade.getStatus() != JePayStatus.COMPLETE) {
                        payTrade.setStatus(JePayStatus.COMPLETE);
                    }

                    return result;
                }

                payTrade.setStatus(JePayStatus.COMPLETE);

            } catch (Exception e) {
                payTrade.setStatus(JePayStatus.ERROR);
                if (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP) {
                    e.printStackTrace();
                }

            } finally {
                BeanService.ME.merge(payTrade);
            }
        }

        return null;
    }
}
