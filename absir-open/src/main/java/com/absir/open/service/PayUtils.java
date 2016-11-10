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

import java.util.Map;

@Configure
public abstract class PayUtils {

    @Inject(value = "PayInterface", type = InjectType.Selectable)
    private static Map<String, IPayInterface> payInterfaceMap;

    @Inject(type = InjectType.Selectable)
    private static IPayProcessor payService;

    public static Object order(String platform, JPayTrade payTrade, Map<String, Object> paramMap)
            throws Exception {
        if (payTrade.getStatus() == null) {
            if (payInterfaceMap != null) {
                if (KernelString.isEmpty(platform)) {
                    platform = payTrade.getPlatform();

                } else {
                    payTrade.setPlatform(platform);
                }

                IPayInterface payInterface = payInterfaceMap.get(platform);
                if (payInterface != null && payInterface instanceof IPayOrder) {
                    payTrade.setPlatform(platform);
                    return ((IPayOrder) payInterface).order(payTrade, paramMap);
                }
            }
        }

        return null;
    }

    public static <T> T getPayInterfaceConfigure(IPayInterface<T> payInterface, String platform, int configureId) {
        if (configureId == 0) {
            return payInterface.getConfigure();
        }

        return null;
    }

    public static boolean validator(int configureId, String platform, String tradeNo, String tradeReceipt, String platformData, float amount, boolean sandbox, String... moreDatas) throws Exception {
        if (payInterfaceMap != null && !KernelString.isEmpty(platform)) {
            IPayInterface<Object> payInterface = payInterfaceMap.get(platform);
            if (payInterface != null) {
                Object configure = getPayInterfaceConfigure(payInterface, platform, configureId);
                if (configure != null) {
                    return payInterface.validator(configure, tradeNo, tradeReceipt, platformData, amount, sandbox, moreDatas);
                }
            }
        }

        return false;
    }

    public static boolean validator(JPayTrade payTrade) throws Exception {
        JePayStatus status = payTrade.getStatus();
        if (status == null || status.compareTo(JePayStatus.ERROR) <= 0) {
            return validator(payTrade.getConfigureId(), payTrade.getPlatform(), payTrade.getTradeNo(), payTrade.getTradeReceipt(), payTrade.getPlatformData(), payTrade.getAmount(), payTrade.isSandbox(), payTrade.getMoreDatas());
        }

        return false;
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
                        if (payStatus == JePayStatus.PAYED || validator(payTrade)) {
                            return processDone(platform, payTrade);
                        }

                    } catch (Exception e) {
                        Environment.throwable(e);
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
                String tradeNo = payTrade.getTradeNo();
                if (KernelString.isEmpty(tradeNo)) {
                    tradeNo = payTrade.getId();
                }

                if (TradeService.ME.addPayHistory(payTrade, tradeNo)) {
                    Object result = payService.process(payTrade);
                    if (payTrade.getStatus() != JePayStatus.COMPLETE) {
                        payTrade.setStatus(JePayStatus.COMPLETE);
                    }

                    return result;
                }

                if (platform != null) {
                    payTrade.setPlatform(platform);
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
