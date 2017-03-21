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
import com.absir.client.helper.HelperJson;
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

    @Inject(value = "PayInterface", type = InjectType.Selectable)
    private static Map<String, IPayInterface> payInterfaceMap;

    @Inject(type = InjectType.Selectable)
    private static IPayProcessor payService;

    public static String order(JPayTrade payTrade, String prepare, Map<String, Object> paramMap)
            throws Exception {
        String id = payTrade.getId();
        if (payInterfaceMap != null) {
            String platform = payTrade.getPlatform();
            if (!KernelString.isEmpty(platform)) {
                IPayInterface payInterface = payInterfaceMap.get(platform);
                if (payInterface != null) {
                    payTrade.setPlatform(platform);
                    String orderInfo = payInterface.order(getPayInterfaceConfigure(payInterface, platform, payTrade.getConfigureId()), payTrade, prepare, paramMap);
                    if (!KernelString.isEmpty(orderInfo)) {
                        id = id + '@' + orderInfo;
                    }
                }
            }
        }

        return id;
    }

    public static <T> T getPayInterfaceConfigure(IPayInterface<T> payInterface, String platform, int configureId) {
        if (configureId == 0) {
            return payInterface.getConfigure();
        }

        return null;
    }

    public static String validator(int configureId, String platform, String tradeNo, String tradeReceipt, String platformData, String goodsId, int goodsNumber, float amount, boolean sandbox, String... moreDatas) throws Exception {
        if (payInterfaceMap != null && !KernelString.isEmpty(platform)) {
            IPayInterface<Object> payInterface = payInterfaceMap.get(platform);
            if (payInterface != null) {
                Object configure = getPayInterfaceConfigure(payInterface, platform, configureId);
                if (configure != null) {
                    return payInterface.validator(configure, tradeNo, tradeReceipt, platformData, goodsId, goodsNumber, amount, sandbox, moreDatas);
                }
            }
        }

        return null;
    }

    public static String validator(JPayTrade payTrade) throws Exception {
        JePayStatus status = payTrade.getStatus();
        if (status == null || status.compareTo(JePayStatus.ERROR) <= 0) {
            return validator(payTrade.getConfigureId(), payTrade.getPlatform(), payTrade.getTradeNo(), payTrade.getTradeReceipt(), payTrade.getPlatformData(), payTrade.getGoodsId(), payTrade.getGoodsNumber(), payTrade.getAmount(), payTrade.isSandbox(), payTrade.getMoreDatas());
        }

        return null;
    }

    public static Object notify(JPayTrade payTrade, String platform, String tradeNo, String tradeReceipt, float amount, String[] moreDatas, JePayStatus payStatus,
                                String statusData) {
        if (payTrade.getStatus() == JePayStatus.COMPLETE) {
            return null;
        }

        payTrade.setPlatform(platform);
        payTrade.setTradeNo(tradeNo);
        payTrade.setTradeReceipt(tradeReceipt);
        payTrade.setMoreDatas(moreDatas);
        payTrade.setStatusData(statusData);
        Object result = null;
        if (payStatus != JePayStatus.COMPLETE) {
            if (payStatus == JePayStatus.PAYING || payStatus == JePayStatus.PAYED) {
                if ((amount < 0 || amount >= payTrade.getAmount())) {
                    try {
                        if (payStatus != JePayStatus.PAYED) {
                            String receipt = validator(payTrade);
                            if (receipt != null) {
                                if (receipt.length() > 0) {
                                    payTrade.setTradeNo(receipt);
                                }

                                payStatus = JePayStatus.PAYED;
                            }
                        }

                        if (payStatus == JePayStatus.PAYED) {
                            return processDone(platform, payTrade);
                        }

                    } catch (Exception e) {
                        LOGGER.error("pay notify error => " + HelperJson.encodeNull(payTrade), e);
                    }
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
        if (payService != null) {
            try {
                if (platform != null) {
                    payTrade.setPlatform(platform);
                }

                if (TradeService.ME.addPayHistory(payTrade)) {
                    Object result = payService.process(payTrade);
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
