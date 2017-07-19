/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-28 下午5:58:27
 */
package com.absir.context.core;

import com.absir.bean.basis.BeanConfig;
import com.absir.bean.basis.Configure;
import com.absir.bean.config.IBeanDefineEager;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.schedule.ScheduleFactory;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.dyna.DynaConvert;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelLang.BreakException;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings({"rawtypes", "unchecked"})
@Inject
@Configure
public abstract class ContextUtils implements IBeanDefineEager {

    private static Charset charset;

    private static ContextFactory contextFactory = BeanFactoryUtils.get(ContextFactory.class);

    private static ScheduleFactory scheduleFactory = BeanFactoryUtils.get(ScheduleFactory.class);

    static {
        BeanConfig beanConfig = BeanFactoryUtils.getBeanConfig();
        charset = beanConfig == null ? null : beanConfig.getExpressionObject("context.charset", null, Charset.class);
        if (charset == null) {
            charset = KernelCharset.getDefault();

        } else {
            KernelCharset.setDefault(charset);
        }

        DynaBinder.INSTANCE.addConvert(new DynaConvert() {

            @Override
            public Object to(Object obj, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                if (toClass == byte[].class && obj instanceof String) {
                    return ((String) obj).getBytes(charset);
                }

                return null;
            }

            @Override
            public Object mapTo(Map<?, ?> map, String name, Class<?> toClass, BreakException breakException)
                    throws Exception {
                return null;
            }
        });
    }

    public static Charset getCharset() {
        return charset;
    }

    public static ContextFactory getContextFactory() {
        return contextFactory;
    }

    public static ScheduleFactory getScheduleFactory() {
        return scheduleFactory;
    }

    public static long getContextTime() {
        return contextFactory.getContextTime();
    }

    public static int getContextShortTime() {
        return contextFactory.getContextShortTime();
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return contextFactory.getThreadPoolExecutor();
    }

    public static <T extends Context<ID>, ID extends Serializable> T getContext(Class<T> ctxClass, ID id) {
        return contextFactory.getContext(ctxClass, id, ctxClass, true);
    }

    public static <T extends Context<ID>, ID extends Serializable> T findContext(Class<T> ctxClass, ID id) {
        Map<Serializable, Context> contextMap = contextFactory.findContextMap(ctxClass);
        if (contextMap != null) {
            Context context = contextMap.get(id);
            if (context != null && ctxClass.isAssignableFrom(context.getClass())) {
                return (T) context;
            }
        }

        return null;
    }
}
