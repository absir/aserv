/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-14 上午10:04:28
 */
package com.absir.open.service.utils;

import com.absir.aserv.configure.conf.ConfigureUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;
import com.absir.open.bean.JPayTrade;
import com.absir.open.bean.value.JePayStatus;
import com.absir.open.service.IPayInterface;
import com.absir.open.service.IPayOrder;
import com.absir.open.service.IPayProcessor;
import com.absir.open.service.TradeService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configure
public abstract class PayUtils {

    @Inject(value = "PayInterface", type = InjectType.Selectable)
    private static Map<String, IPayInterface> payInterfaceMap;

    @Inject(type = InjectType.Selectable)
    private static IPayProcessor payService;

    public static Object order(String platform, String channel, JPayTrade payTrade, Map<String, Object> paramMap)
            throws Exception {
        if (payTrade.getStatus() == null) {
            if (payInterfaceMap != null) {
                IPayInterface payInterface = payInterfaceMap.get(platform);
                if (payInterface != null && payInterface instanceof IPayOrder) {
                    payTrade.setPlatform(platform);
                    payTrade.setChannel(channel);
                    return ((IPayOrder) payInterface).order(payTrade, paramMap);
                }
            }
        }

        return null;
    }

    private static Map<String, Object> payInterfaceIdMapConfigure = new HashMap<String, Object>();

    protected static <T> T getPayInterfaceConfigure(IPayInterface<T> payInterface, String platform, int configureId) {
        String id = platform + "@" + configureId;
        Object configure = payInterfaceIdMapConfigure.get(id);
        if (configure == null) {
            synchronized (payInterfaceIdMapConfigure) {
                configure = payInterfaceIdMapConfigure.get(id);
                if (configure == null) {
                    Class<T> configureClass = KernelClass.typeClass(payInterface.getClass(), IPayInterface.TYPE_VARIABLE);
                    File configureFile = new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "pay/" + configureClass.getSimpleName() + (configureId <= 0 ? "" : configureId) + ".conf");
                    if (configureFile.exists()) {
                        configure = ConfigureUtils.newConfigure(configureClass, configureFile);
                        payInterfaceIdMapConfigure.put(id, configure);
                    }
                }
            }
        }

        return (T) configure;
    }

    public static boolean validator(String platform, String tradeNo, String tradeReceipt, String platformData, String[] moreDatas, int configureId) throws Exception {
        if (payInterfaceMap != null) {
            IPayInterface<Object> payInterface = payInterfaceMap.get(platform);
            if (payInterface != null) {
                Object configure = getPayInterfaceConfigure(payInterface, platform, configureId);
                if (configure != null) {
                    return payInterface.validator(configure, tradeNo, tradeReceipt, platformData, moreDatas);
                }
            }
        }

        return false;
    }

    public static boolean validator(String platform, JPayTrade payTrade) throws Exception {
        JePayStatus status = payTrade.getStatus();
        if (status == null || status.compareTo(JePayStatus.ERROR) <= 0) {
            return validator(platform, payTrade.getTradeNo(), payTrade.getTradeReceipt(), payTrade.getPlatformData(), payTrade.getMoreDatas(), payTrade.getConfigureId());
        }

        return false;
    }

    public static Object process(String platform, JPayTrade payTrade) throws Exception {
        if (validator(platform, payTrade)) {
            payTrade.setPlatform(platform);
            return processDone(platform, payTrade);
        }

        return null;
    }

    public static Object processDone(String platform, JPayTrade payTrade) {
        if (payService != null) {
            try {
                String tradeNo = payTrade.getTradeNo();
                if (KernelString.isEmpty(tradeNo)) {
                    tradeNo = payTrade.getId();
                }

                if (TradeService.ME.addPayHistory(payTrade, tradeNo)) {
                    Object res = payService.process(payTrade);
                    if (payTrade.getStatus() != JePayStatus.COMPLETE) {
                        payTrade.setStatus(JePayStatus.COMPLETE);
                    }

                    return res;

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

    public static Object notify(JPayTrade payTrade, String platform, String tradeNo, String tradeReceipt, String[] moreDatas, JePayStatus payStatus,
                                String statusData) {
        if (payStatus != JePayStatus.COMPLETE) {
            if (payStatus == JePayStatus.PAYED) {
                payTrade.setStatusData(statusData);
                payTrade.setPlatform(platform);
                payTrade.setTradeNo(tradeNo);
                payTrade.setTradeReceipt(tradeReceipt);
                payTrade.setMoreDatas(moreDatas);
                return processDone(platform, payTrade);

            } else if (payStatus != payTrade.getStatus()) {
                payTrade.setStatus(payStatus);
                BeanService.ME.merge(payTrade);
                return Boolean.TRUE;
            }
        }

        return null;
    }
}
